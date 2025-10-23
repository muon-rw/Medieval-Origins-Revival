package dev.muon.medievalorigins.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Thread-safe cache for expensive PowerHolderComponent lookups.
 * Uses entity IDs to avoid memory leaks and automatic cleanup on tick.
 */
public class PowerCache {
    // Cache structure: EntityId -> PowerClass -> List of cached power types
    private static final Map<Integer, EntityCacheEntry> cache = new ConcurrentHashMap<>();
    
    // Tick counter for periodic cleanup
    private static long tickCounter = 0;
    private static final int CLEANUP_INTERVAL = 600; // Clean every 30 seconds (20 ticks/sec * 30)
    private static final long MAX_AGE_TICKS = 6000; // 5 minutes
    
    private static class EntityCacheEntry {
        final Map<Class<?>, CachedPowerData<?>> powerDataMap = new ConcurrentHashMap<>();
        long lastAccessTick = tickCounter;
        final UUID entityUuid; // Validate we're caching the right entity
        
        EntityCacheEntry(UUID entityUuid) {
            this.entityUuid = entityUuid;
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
        EntityCacheEntry entry = cache.get(entityId);
        
        // Validate cached entry belongs to this entity (guards against ID reuse)
        if (entry != null && !entry.entityUuid.equals(entityUuid)) {
            cache.remove(entityId); // Stale entry from different entity
            entry = null;
        }
        
        if (entry == null) {
            entry = new EntityCacheEntry(entityUuid);
            cache.put(entityId, entry);
        }
        
        entry.lastAccessTick = tickCounter;
        
        CachedPowerData<?> cached = entry.powerDataMap.get(powerClass);
        if (cached != null) {
            return (List<T>) cached.powerTypes;
        }
        
        // Compute and cache
        List<T> powerTypes = PowerHolderComponent.getPowerTypes(entity, powerClass);
        entry.powerDataMap.put(powerClass, new CachedPowerData<>(powerTypes, !powerTypes.isEmpty()));
        
        return powerTypes;
    }
    
    /**
     * Check if entity has a power type (cached).
     */
    public static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> powerClass) {
        if (entity == null || entity.isRemoved()) return false;
        
        int entityId = entity.getId();
        UUID entityUuid = entity.getUUID();
        EntityCacheEntry entry = cache.get(entityId);
        
        // Validate cached entry belongs to this entity
        if (entry != null && !entry.entityUuid.equals(entityUuid)) {
            cache.remove(entityId);
            entry = null;
        }
        
        if (entry != null) {
            entry.lastAccessTick = tickCounter;
            CachedPowerData<?> cached = entry.powerDataMap.get(powerClass);
            if (cached != null) {
                return cached.hasAny;
            }
        }
        
        // Compute and cache
        List<T> powerTypes = getPowerTypes(entity, powerClass);
        return !powerTypes.isEmpty();
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
        
        EntityCacheEntry entry = cache.get(entity.getId());
        if (entry != null) {
            entry.powerDataMap.remove(powerClass);
        }
    }
    
    /**
     * Clear all cached data.
     */
    public static void clearAll() {
        cache.clear();
        tickCounter = 0;
    }
    
    /**
     * Periodic cleanup of stale cache entries.
     */
    public static void tick() {
        tickCounter++;
        
        if (tickCounter % CLEANUP_INTERVAL == 0) {
            cleanupStaleEntries();
        }
    }
    
    /**
     * Remove cache entries that haven't been accessed recently.
     */
    private static void cleanupStaleEntries() {
        long cutoffTick = tickCounter - MAX_AGE_TICKS;
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
        return new CacheStats(totalEntries, totalPowerTypes, tickCounter);
    }
    
    public static record CacheStats(int cachedEntities, int cachedPowerTypes, long currentTick) {}
}
