package dev.muon.medievalorigins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.codec.LoggedOptionalField;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.EntityCondition;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.data.Comparison;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public final class EntityInRadiusCondition implements ConditionType<EntityCtx, EntityInRadiusCondition.Cfg> {
    public record Cfg(Optional<EntityCondition> condition, double radius, int compareTo, Comparison comparison) {}

    @Override
    public MapCodec<Cfg> codec() {
        return RecordCodecBuilder.mapCodec(i -> i.group(
                LoggedOptionalField.of("condition", EntityCondition.CODEC).forGetter(Cfg::condition),
                Codec.DOUBLE.fieldOf("radius").forGetter(Cfg::radius),
                LoggedOptionalField.of("compare_to", Codec.INT, 1).forGetter(Cfg::compareTo),
                LoggedOptionalField.of("comparison", Comparison.CODEC, Comparison.GREATER_EQUAL).forGetter(Cfg::comparison)
        ).apply(i, Cfg::new));
    }

    @Override
    public boolean test(Cfg cfg, EntityCtx ctx) {
        Entity entity = ctx.raw();
        int compareTo = cfg.compareTo();
        int stopAt = -1;
        switch (cfg.comparison()) {
            case EQUAL, LESS_EQUAL, GREATER -> stopAt = compareTo + 1;
            case LESS, GREATER_EQUAL -> stopAt = compareTo;
            default -> {
            }
        }

        int count = 0;
        for (Entity target : entity.level().getEntities(entity, entity.getBoundingBox().inflate(cfg.radius()))) {
            if (target != null && (cfg.condition().isEmpty() || cfg.condition().get().test(EntityCtx.of(target, target.level())))) {
                count++;
                if (count == stopAt) {
                    break;
                }
            }
        }
        return cfg.comparison().compare(count, compareTo);
    }
}