package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class ClearNegativeEffectsAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            List<MobEffect> effectsToRemove = livingEntity.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> !effect.isBeneficial())
                    .toList();

            effectsToRemove.forEach(livingEntity::removeEffect);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("clear_negative_effects"),
                new SerializableData(),
                ClearNegativeEffectsAction::action
        );
    }
}