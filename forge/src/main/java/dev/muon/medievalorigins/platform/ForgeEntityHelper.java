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
import java.util.function.Supplier;

/**
 * Forge implementation for entity type access.
 */
public class ForgeEntityHelper implements IEntityHelper {

    private static final Map<Class<? extends Entity>, Supplier<EntityType<?>>> ENTITY_TYPES = new HashMap<>();

    static {
        // Forge uses RegistryObject which requires .get(), so we store suppliers
        ENTITY_TYPES.put(SummonedSkeleton.class, () -> ModEntities.SUMMON_SKELETON.get());
        ENTITY_TYPES.put(SummonedZombie.class, () -> ModEntities.SUMMON_ZOMBIE.get());
        ENTITY_TYPES.put(SummonedWitherSkeleton.class, () -> ModEntities.SUMMON_WITHER_SKELETON.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> EntityType<T> getEntityType(Class<T> entityClass) {
        Supplier<EntityType<?>> supplier = ENTITY_TYPES.get(entityClass);
        if (supplier == null) {
            throw new IllegalArgumentException("No EntityType registered for " + entityClass.getName());
        }
        return (EntityType<T>) supplier.get();
    }
}

