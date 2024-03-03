package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ModEntityConditions {
    public static void register() {
        register(new ConditionFactory<>(MedievalOrigins.loc("golden_tool"), new SerializableData(), (data, entity)
                -> entity instanceof AbstractArrow));
    }

    private static void register(ConditionFactory<Entity> serializer) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, serializer.getSerializerId(), serializer);
    }
}