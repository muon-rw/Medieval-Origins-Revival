package dev.muon.medievalorigins.action;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;

public class ClearNegativeEffectsAction extends EntityAction<NoConfiguration> {

    public ClearNegativeEffectsAction() {
        super(NoConfiguration.CODEC);
    }

    @Override
    public void execute(NoConfiguration configuration, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> !effect.isBeneficial())
                    .forEach(livingEntity::removeEffect);
        }
    }
}