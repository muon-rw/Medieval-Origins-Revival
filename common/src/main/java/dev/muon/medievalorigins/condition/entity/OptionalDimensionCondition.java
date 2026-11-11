package dev.muon.medievalorigins.condition.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.EntityCtx;
import net.minecraft.resources.ResourceLocation;

public final class OptionalDimensionCondition implements ConditionType<EntityCtx, OptionalDimensionCondition.Cfg> {
    public record Cfg(ResourceLocation dimension) {}

    @Override
    public MapCodec<Cfg> codec() {
        return RecordCodecBuilder.mapCodec(i -> i.group(
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(Cfg::dimension)
        ).apply(i, Cfg::new));
    }

    @Override
    public boolean test(Cfg cfg, EntityCtx ctx) {
        return ctx.raw().level().dimension().location().equals(cfg.dimension());
    }
}