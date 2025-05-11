package dev.muon.medievalorigins.action;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;
import java.util.stream.Collectors;

public class ClearNegativeEffectsAction extends EntityAction<NoConfiguration> {

    public ClearNegativeEffectsAction() {
        super(NoConfiguration.CODEC);
    }

    @Override
    public void execute(NoConfiguration configuration, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            List<MobEffect> effectsToRemove = livingEntity.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> !effect.isBeneficial())
                    .toList();

            effectsToRemove.forEach(livingEntity::removeEffect);
        }
    }
}