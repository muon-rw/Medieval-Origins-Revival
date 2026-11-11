package dev.muon.medievalorigins.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.attribute.ModAttributes;
import dev.muon.medievalorigins.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

import java.util.Map;

public final class ModEntities {
    public static final Holder<EntityType<SummonedSkeleton>> SUMMON_SKELETON = register("summon_skeleton",
            EntityType.Builder.<SummonedSkeleton>of(SummonedSkeleton::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.8F)
                    .clientTrackingRange(10));

    public static final Holder<EntityType<SummonedZombie>> SUMMON_ZOMBIE = register("summon_zombie",
            EntityType.Builder.<SummonedZombie>of(SummonedZombie::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.8F)
                    .clientTrackingRange(10));

    public static final Holder<EntityType<SummonedWitherSkeleton>> SUMMON_WITHER_SKELETON = register("summon_wither_skeleton",
            EntityType.Builder.<SummonedWitherSkeleton>of(SummonedWitherSkeleton::new, MobCategory.CREATURE)
                    .sized(1.0F, 2.1F)
                    .clientTrackingRange(10));

    public static final Holder<EntityType<SummonedArrow>> SUMMONED_ARROW = register("summoned_arrow",
            EntityType.Builder.<SummonedArrow>of(SummonedArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20));

    private ModEntities() {
    }

    public static void init() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> Holder<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        String id = MedievalOrigins.loc(name).toString();
        Holder<EntityType<?>> holder = Services.REGISTRAR.register(BuiltInRegistries.ENTITY_TYPE, MedievalOrigins.loc(name), () -> builder.build(id));
        return (Holder<EntityType<T>>) (Holder<?>) holder;
    }

    /** Called by each loader once entity types are registered. */
    public static Map<EntityType<? extends LivingEntity>, AttributeSupplier> createAttributes() {
        return Map.of(
                SUMMON_SKELETON.value(), Monster.createMonsterAttributes()
                        .add(Attributes.MOVEMENT_SPEED, 0.25D)
                        .add(ModAttributes.RANGED_DAMAGE)
                        .build(),
                SUMMON_WITHER_SKELETON.value(), Monster.createMonsterAttributes()
                        .add(Attributes.MOVEMENT_SPEED, 0.25D)
                        .add(ModAttributes.RANGED_DAMAGE)
                        .build(),
                SUMMON_ZOMBIE.value(), Monster.createMonsterAttributes()
                        .add(Attributes.MOVEMENT_SPEED, 0.18D)
                        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D)
                        .build()
        );
    }
}
