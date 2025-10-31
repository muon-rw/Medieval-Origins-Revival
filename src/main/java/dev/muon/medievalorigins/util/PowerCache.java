package dev.muon.medievalorigins.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * Thread-safe cache for expensive PowerHolderComponent lookups.
 * Invalidates upon entity unload and periodically.
 * TODO: Invalidate cache on power changes
 * Should be identical between Client and Server
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

        int entityId = entity.getId();
        UUID entityUuid = entity.getUUID();

        EntityCacheEntry entry = cache.compute(entityId, (id, existing) -> {
            if (existing == null || !existing.entityUuid.equals(entityUuid)) {
                return new EntityCacheEntry(entityUuid);
            }
            existing.lastAccessTick = tickCounter.get();
            return existing;
        });

        // Atomically get or compute the power data
        // The computeIfAbsent block is only executed if powerClass is not in the map
        CachedPowerData<?> cached = entry.powerDataMap.computeIfAbsent(powerClass, pc -> {
            List<T> powerTypes = PowerHolderComponent.getPowerTypes(entity, powerClass);
            return new CachedPowerData<>(powerTypes, !powerTypes.isEmpty());
        });

        return (List<T>) cached.powerTypes;
    }

    /**
     * Check if entity has a power type (cached).
     */
    public static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> powerClass) {
        if (entity == null || entity.isRemoved()) return false;

        int entityId = entity.getId();
        UUID entityUuid = entity.getUUID();

        EntityCacheEntry entry = cache.compute(entityId, (id, existing) -> {
            if (existing == null || !existing.entityUuid.equals(entityUuid)) {
                return null; // Don't create, just invalidate
            }
            existing.lastAccessTick = tickCounter.get();
            return existing;
        });


        if (entry != null) {
            // Check if data is already cached
            CachedPowerData<?> cached = entry.powerDataMap.get(powerClass);
            if (cached != null) {
                return cached.hasAny;
            }
        }

        // If not cached or entry didn't exist, fall back to getPowerTypes,
        // which will safely compute and cache it.
        return !getPowerTypes(entity, powerClass).isEmpty();
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
