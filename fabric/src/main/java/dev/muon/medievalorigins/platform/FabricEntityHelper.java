package dev.muon.medievalorigins.platform;

import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonedSkeleton;
import dev.muon.medievalorigins.entity.SummonedWitherSkeleton;
import dev.muon.medievalorigins.entity.SummonedZombie;
import dev.muon.medievalorigins.platform.services.IEntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Fabric implementation for entity type access.
 */
public class FabricEntityHelper implements IEntityHelper {

    private static final Map<Class<? extends Entity>, EntityType<?>> ENTITY_TYPES = new HashMap<>();

    static {
        ENTITY_TYPES.put(SummonedSkeleton.class, ModEntities.SUMMON_SKELETON);
        ENTITY_TYPES.put(SummonedZombie.class, ModEntities.SUMMON_ZOMBIE);
        ENTITY_TYPES.put(SummonedWitherSkeleton.class, ModEntities.SUMMON_WITHER_SKELETON);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> EntityType<T> getEntityType(Class<T> entityClass) {
        EntityType<?> type = ENTITY_TYPES.get(entityClass);
        if (type == null) {
            throw new IllegalArgumentException("No EntityType registered for " + entityClass.getName());
        }
        return (EntityType<T>) type;
    }
}

