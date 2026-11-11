package dev.muon.medievalorigins.condition.entity;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.entity.player.Player;

public final class LookingDownCondition implements ConditionType<EntityCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, EntityCtx ctx) {
        if (ctx.raw() instanceof Player player) {
            float pitch = player.getXRot();
            return pitch >= 70.0f && pitch <= 90.0f;
        }
        return false;
    }
}