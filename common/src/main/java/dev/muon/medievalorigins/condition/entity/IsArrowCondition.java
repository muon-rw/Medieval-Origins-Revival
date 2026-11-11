package dev.muon.medievalorigins.condition.entity;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.entity.projectile.AbstractArrow;

public final class IsArrowCondition implements ConditionType<EntityCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, EntityCtx ctx) {
        return ctx.raw() instanceof AbstractArrow;
    }
}