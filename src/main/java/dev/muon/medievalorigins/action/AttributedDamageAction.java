package dev.muon.medievalorigins.action;


import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.DamageSourceDescription;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttributedDamageAction {
    public AttributedDamageAction() {
    }

    public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ActionFactory<>(MedievalOrigins.loc("damage"),
                new SerializableData()
                        .add("amount", SerializableDataTypes.FLOAT)
                        .add("attribute", SerializableDataTypes.IDENTIFIER, null)
                        .add("operation", SerializableDataTypes.MODIFIER_OPERATION)
                        .add("source", ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, null)
                        .add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null),
                AttributedDamageAction::action);
    }

    public static void action(SerializableData.Instance data, Tuple<Entity, Entity> entities) {
        Entity actor = entities.getA();
        Entity target = entities.getB();

        if (!(actor instanceof LivingEntity) || target == null) {
            return;
        }

        float amount = data.get("amount");
        ResourceLocation attributeLocation = data.get("attribute");
        ResourceKey<Attribute> attributeKey = ResourceKey.create(Registries.ATTRIBUTE, attributeLocation);
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeKey);
        AttributeModifier.Operation operation = data.get("operation");

        if (attribute != null) {
            LivingEntity livingActor = (LivingEntity) actor;
            double attributeValue = livingActor.getAttributeValue(attribute);

            switch (operation) {
                case ADDITION:
                    amount += (float) attributeValue;
                    break;
                case MULTIPLY_BASE:
                    amount *= (float) (1 + attributeValue);
                    break;
                case MULTIPLY_TOTAL:
                    amount *= (float) attributeValue;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + operation);
            }
        }

        DamageSource source = MiscUtil.createDamageSource(
                actor.damageSources(),
                data.get("source"),
                data.get("damage_type"),
                actor);
        target.hurt(source, amount);
    }
}
