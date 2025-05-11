package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class LookingDownCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            float pitch = livingEntity.getXRot();
            return pitch >= 70.0f && pitch <= 90.0f;
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                MedievalOrigins.loc("looking_down"),
                new SerializableData(),
                LookingDownCondition::condition
        );
    }

}