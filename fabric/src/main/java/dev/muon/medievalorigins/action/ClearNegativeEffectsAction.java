package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;

public class ClearNegativeEffectsAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> !effect.isBeneficial())
                    .forEach(livingEntity::removeEffect);
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