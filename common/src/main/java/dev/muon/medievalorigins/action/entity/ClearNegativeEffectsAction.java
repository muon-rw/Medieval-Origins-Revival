package dev.muon.medievalorigins.action.entity;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public final class ClearNegativeEffectsAction implements ActionType<EntityCtx, EmptyCfg> {

    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public void run(EmptyCfg cfg, EntityCtx ctx) {
        LivingEntity living = ctx.living();
        if (living == null) {
            return;
        }
        // getActiveEffects() is a live view of the effect map, so snapshot before removing
        List<Holder<MobEffect>> negative = living.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect)
                .filter(effect -> !effect.value().isBeneficial())
                .toList();
        negative.forEach(living::removeEffect);
    }
}
