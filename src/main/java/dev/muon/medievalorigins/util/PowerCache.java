package dev.muon.medievalorigins.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * Thread-safe cache for expensive PowerHolderComponent.getPowerTypes() lookups.
 *
 * <p>Caches the list of PowerType instances for each (Entity, PowerClass) pair.
 * Returned PowerType instances are live objects - calling {@link PowerType#isActive()}
 * will evaluate the current condition state, not a cached state.
 *
 * <p><b>Usage:</b>
 * This cache provides drop-in replacements for {@code PowerHolderComponent} methods.
 * Simply replace:
 * <pre>{@code
 * PowerHolderComponent.getPowerTypes(entity, MyPowerType.class)
 * PowerHolderComponent.hasPowerType(entity, MyPowerType.class)
 * }</pre>
 * with:
 * <pre>{@code
 * PowerCache.getPowerTypes(entity, MyPowerType.class)
 * PowerCache.hasPowerType(entity, MyPowerType.class)
 * }</pre>
 * The behavior is identical, but subsequent calls with the same entity/class pair
 * will use cached results instead of re-querying the component.
 *
 * <p><b>Performance Benefits:</b>
 * The uncached {@code PowerHolderComponent.getPowerTypes()} method has cumulative overhead
 * when called repeatedly:
 * <ul>
 *   <li>Iterates through <b>all</b> power types in the entity's ConcurrentHashMap</li>
 *   <li>Allocates a new LinkedList for every single call</li>
 *   <li>Performs stream operations (filter, map, collect) on every call</li>
 *   <li>Most critically: the same entity/power class pair is often queried multiple times per tick</li>
 * </ul>
 * This cache is most beneficial when power queries are repeated frequently (e.g., AI targeting checks
 * every tick, repeated combat calculations, rendering updates). The aggregate savings from avoiding
 * repeated iteration and collection allocation add up significantly in multiplayer environments
 * with many entities checking each other's powers.
 *
 * <p><b>Memory Optimization:</b>
 * This cache references entities by their numeric ID and UUID rather than storing direct
 * entity references. This approach significantly reduces memory overhead and prevents
 * potential memory leaks from retaining entity references after they should be garbage
 * collected. The UUID validation ensures correctness when entity IDs are reused.
 *
 * <p><b>Cache Invalidation:</b>
 * <ul>
 *   <li>LRU cleanup every 30s (600 ticks), removing entries older than 5 minutes</li>
 *   <li>Automatically invalidates on power add/remove via {@code PowerHolderComponentImplMixin}</li>
 *   <li><b>Server-side:</b> Entity unload via {@code ServerEntityEvents.ENTITY_UNLOAD},
 *       cache clear on server stop via {@code ServerLifecycleEvents.SERVER_STOPPING}</li>
 *   <li><b>Client-side:</b> Entity unload via {@code ClientEntityEvents.ENTITY_UNLOAD},
 *       cache clear on disconnect via {@code ClientPlayConnectionEvents.DISCONNECT}</li>
 *   <li>Manual invalidation via {@link #invalidate(Entity)} or {@link #invalidate(Entity, Class)}</li>
 *   <li>UUID validation: Detects and handles entity ID reuse</li>
 * </ul>
 *
 * <p><b>Thread-Safety:</b> All public methods are safe for concurrent access using
 * {@code ConcurrentHashMap} and atomic operations.
 *
 * @see PowerHolderComponent#getPowerTypes(Entity, Class)
 */
public class PowerCache {
    // Cache structure: EntityId -> PowerClass -> List of cached power types
    private static final Map<Integer, EntityCacheEntry> cache = new ConcurrentHashMap<>();
    
    // Tick counter for periodic cleanup
    private static final AtomicLong tickCounter = new AtomicLong(0);
    private static final int CLEANUP_INTERVAL = 600; // Clean every 30 seconds (20 ticks/sec * 30)
    private static final long MAX_AGE_TICKS = 6000; // 5 minutes
    
    private static class EntityCacheEntry {
        final Map<Class<?>, CachedPowerData<?>> powerDataMap = new ConcurrentHashMap<>();
        volatile long lastAccessTick;
        final UUID entityUuid; // Validate we're caching the right entity

        EntityCacheEntry(UUID entityUuid) {
            this.entityUuid = entityUuid;
            this.lastAccessTick = tickCounter.get(); // Read current value
        }
    }

    private static class CachedPowerData<T extends PowerType> {
        final List<T> powerTypes;
        final boolean hasAny;

        CachedPowerData(List<T> powerTypes, boolean hasAny) {
            this.powerTypes = Collections.unmodifiableList(new ArrayList<>(powerTypes));
            this.hasAny = hasAny;
        }
    }

    /**
     * Get or compute cached power types for an entity.
     */
    @SuppressWarnings("unchecked")
    public static <T extends PowerType> List<T> getPowerTypes(Entity entity, Class<T> powerClass) {
        if (entity == null || entity.isRemoved()) return Collections.emptyList();

        EntityCacheEntry entry = getOrCreateEntityEntry(entity);

        CachedPowerData<?> cached = entry.powerDataMap.computeIfAbsent(powerClass, pc -> {
            List<T> powerTypes = PowerHolderComponent.getPowerTypes(entity, powerClass);
            return new CachedPowerData<>(powerTypes, !powerTypes.isEmpty());
        });

        return (List<T>) cached.powerTypes;
    }

    /**
     * Check if entity has ANY power types of this class (regardless of active state).
     * To check for active powers, use: hasPowerType(entity, powerClass, PowerType::isActive)
     */
    public static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> powerClass) {
        if (entity == null || entity.isRemoved()) return false;

        // 1. Use the helper method
        EntityCacheEntry entry = getOrCreateEntityEntry(entity);

        // 2. Try a fast 'get' first. This is non-blocking and very cheap.
        CachedPowerData<?> cached = entry.powerDataMap.get(powerClass);
        if (cached != null) {
            return cached.hasAny; // Fast path: cache hit
        }

        // 3. Slow path: cache miss. Compute, store, and return.
        //    This is the same logic as getPowerTypes, but we return 'hasAny'.
        CachedPowerData<?> newlyCached = entry.powerDataMap.computeIfAbsent(powerClass, pc -> {
            // This lambda only runs if 'cached' was null and another thread
            // didn't just add it in the meantime.
            List<T> powerTypes = PowerHolderComponent.getPowerTypes(entity, powerClass);
            return new CachedPowerData<>(powerTypes, !powerTypes.isEmpty());
        });

        return newlyCached.hasAny;
    }
    
    /**
     * Check if entity has a power type matching a predicate (cached).
     */
    public static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> powerClass, Predicate<T> filter) {
        if (entity == null || entity.isRemoved()) return false;
        
        List<T> powerTypes = getPowerTypes(entity, powerClass);
        return powerTypes.stream().anyMatch(filter);
    }
    
    /**
     * Get the first power type matching a predicate (uses cache).
     */
    public static <T extends PowerType> Optional<T> getFirstPowerType(Entity entity, Class<T> powerClass, Predicate<T> filter) {
        if (entity == null || entity.isRemoved()) return Optional.empty();
        
        List<T> powerTypes = getPowerTypes(entity, powerClass);
        return powerTypes.stream().filter(filter).findFirst();
    }

    /**
     * Gets or creates the cache entry for a given entity, handling
     * UUID validation and access tick updates.
     */
    private static EntityCacheEntry getOrCreateEntityEntry(Entity entity) {
        int entityId = entity.getId();
        UUID entityUuid = entity.getUUID();

        return cache.compute(entityId, (id, existing) -> {
            // If new or UUID mismatch, create a new entry
            if (existing == null || !existing.entityUuid.equals(entityUuid)) {
                return new EntityCacheEntry(entityUuid);
            }
            // Otherwise, update access tick and return existing
            existing.lastAccessTick = tickCounter.get();
            return existing;
        });
    }
    
    /**
     * Invalidate all cached data for an entity.
     */
    public static void invalidate(Entity entity) {
        if (entity == null) return;
        cache.remove(entity.getId());
    }
    
    /**
     * Invalidate specific power class cache for an entity.
     */
    public static void invalidate(Entity entity, Class<? extends PowerType> powerClass) {
        if (entity == null) return;

        int entityId = entity.getId();
        UUID entityUuid = entity.getUUID();

        cache.computeIfPresent(entityId, (id, existing) -> {
            if (existing.entityUuid.equals(entityUuid)) {
                existing.powerDataMap.remove(powerClass);
                // Return existing to keep the entry, or null to remove it entirely
                return existing.powerDataMap.isEmpty() ? null : existing;
            }
            return existing; // Wrong UUID, don't modify
        });
    }
    
    /**
     * Clear all cached data.
     */
    public static void clearAll() {
        cache.clear();
        tickCounter.set(0);
    }
    
    /**
     * Periodic cleanup of stale cache entries.
     */
    public static void tick() {
        tickCounter.incrementAndGet();
        
        if (tickCounter.get() % CLEANUP_INTERVAL == 0) {
            cleanupStaleEntries();
        }
    }
    
    /**
     * Remove cache entries that haven't been accessed recently.
     */
    private static void cleanupStaleEntries() {
        long cutoffTick = tickCounter.get() - MAX_AGE_TICKS;
        cache.entrySet().removeIf(entry -> entry.getValue().lastAccessTick < cutoffTick);
    }
    
    /**
     * Get cache statistics for monitoring.
     */
    public static CacheStats getStats() {
        int totalEntries = cache.size();
        int totalPowerTypes = cache.values().stream()
                .mapToInt(entry -> entry.powerDataMap.size())
                .sum();
        return new CacheStats(totalEntries, totalPowerTypes, tickCounter.get());
    }
    
    public static record CacheStats(int cachedEntities, int cachedPowerTypes, long currentTick) {}
}
