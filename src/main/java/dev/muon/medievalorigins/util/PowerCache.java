package dev.muon.medievalorigins.util;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.world.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Thread-safe cache for expensive PowerHolderComponent lookups.
 * Uses weak references to avoid memory leaks when entities are removed.
 */
public class PowerCache {
    // Cache structure: Entity -> PowerClass -> List of cached power types
    private static final Map<WeakReference<Entity>, Map<Class<?>, CachedPowerData<?>>> cache = new ConcurrentHashMap<>();
    
    // Track weak references for cleanup
    private static final Map<Entity, WeakReference<Entity>> entityRefs = new WeakHashMap<>();
    
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
        if (entity == null) return Collections.emptyList();
        
        WeakReference<Entity> ref = getOrCreateRef(entity);
        Map<Class<?>, CachedPowerData<?>> entityCache = cache.computeIfAbsent(ref, k -> new ConcurrentHashMap<>());
        
        CachedPowerData<?> cached = entityCache.get(powerClass);
        if (cached != null) {
            return (List<T>) cached.powerTypes;
        }
        
        // Compute and cache
        List<T> powerTypes = PowerHolderComponent.getPowerTypes(entity, powerClass);
        entityCache.put(powerClass, new CachedPowerData<>(powerTypes, !powerTypes.isEmpty()));
        
        return powerTypes;
    }
    
    /**
     * Check if entity has a power type (cached).
     */
    public static <T extends PowerType> boolean hasPowerType(Entity entity, Class<T> powerClass) {
        if (entity == null) return false;
        
        WeakReference<Entity> ref = getOrCreateRef(entity);
        Map<Class<?>, CachedPowerData<?>> entityCache = cache.get(ref);
        
        if (entityCache != null) {
            CachedPowerData<?> cached = entityCache.get(powerClass);
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
        if (entity == null) return false;
        
        List<T> powerTypes = getPowerTypes(entity, powerClass);
        return powerTypes.stream().anyMatch(filter);
    }
    
    /**
     * Get the first power type matching a predicate (uses cache).
     */
    public static <T extends PowerType> Optional<T> getFirstPowerType(Entity entity, Class<T> powerClass, Predicate<T> filter) {
        if (entity == null) return Optional.empty();
        
        List<T> powerTypes = getPowerTypes(entity, powerClass);
        return powerTypes.stream().filter(filter).findFirst();
    }
    
    /**
     * Invalidate all cached data for an entity.
     */
    public static void invalidate(Entity entity) {
        if (entity == null) return;
        
        WeakReference<Entity> ref = entityRefs.get(entity);
        if (ref != null) {
            cache.remove(ref);
            entityRefs.remove(entity);
        }
        
        // Clean up stale references while we're at it
        cleanupStaleReferences();
    }
    
    /**
     * Invalidate specific power class cache for an entity.
     */
    public static void invalidate(Entity entity, Class<? extends PowerType> powerClass) {
        if (entity == null) return;
        
        WeakReference<Entity> ref = entityRefs.get(entity);
        if (ref != null) {
            Map<Class<?>, CachedPowerData<?>> entityCache = cache.get(ref);
            if (entityCache != null) {
                entityCache.remove(powerClass);
            }
        }
    }
    
    /**
     * Clear all cached data.
     */
    public static void clearAll() {
        cache.clear();
        entityRefs.clear();
    }
    
    private static WeakReference<Entity> getOrCreateRef(Entity entity) {
        return entityRefs.computeIfAbsent(entity, WeakReference::new);
    }
    
    private static void cleanupStaleReferences() {
        cache.entrySet().removeIf(entry -> entry.getKey().get() == null);
    }
}
