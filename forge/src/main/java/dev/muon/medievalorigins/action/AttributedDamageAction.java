package dev.muon.medievalorigins.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.DamageSourceDescription;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

import java.util.Optional;

public class AttributedDamageAction extends BiEntityAction<AttributedDamageAction.Configuration> {

    public AttributedDamageAction() {
        super(Configuration.CODEC);
    }

    @Override
    public void execute(Configuration configuration, Entity actor, Entity target) {
        if (!(actor instanceof LivingEntity) || target == null) {
            return;
        }

        float baseDamage = configuration.base();
        Configuration.Modifier modifier = configuration.modifier().orElse(null);

        if (modifier != null) {
            ResourceLocation attributeLocation = modifier.attribute();
            ResourceKey<Attribute> attributeKey = ResourceKey.create(Registries.ATTRIBUTE, attributeLocation);
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeKey);
            AttributeModifier.Operation operation = modifier.operation();
            float modifierValue = modifier.value();

            if (attribute != null) {
                LivingEntity livingActor = (LivingEntity) actor;
                double attributeValue = livingActor.getAttributeValue(attribute);

                switch (operation) {
                    case ADDITION:
                        baseDamage += (attributeValue * modifierValue);
                        break;
                    case MULTIPLY_BASE:
                        baseDamage += baseDamage * (attributeValue) * (modifierValue);
                        break;
                    case MULTIPLY_TOTAL:
                        baseDamage += baseDamage * (attributeValue) * (modifierValue);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operation: " + operation);
                }
            }
        }

        DamageSource source = MiscUtil.createDamageSource(
                actor.damageSources(),
                configuration.source().orElse(null),
                configuration.damageType().orElse(null),
                actor);

        target.hurt(source, baseDamage);
    }

    public static class Configuration implements IDynamicFeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("base").forGetter(Configuration::base),
                CalioCodecHelper.optionalField(Modifier.CODEC, "modifier").forGetter(Configuration::modifier),
                CalioCodecHelper.optionalField(ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, "source").forGetter(Configuration::source),
                CalioCodecHelper.optionalField(SerializableDataTypes.DAMAGE_TYPE, "damage_type").forGetter(Configuration::damageType)
        ).apply(instance, Configuration::new));

        private final float base;
        private final Optional<Modifier> modifier;
        private final Optional<DamageSourceDescription> source;
        private final Optional<ResourceKey<DamageType>> damageType;

        public Configuration(float base, Optional<Modifier> modifier, Optional<DamageSourceDescription> source, Optional<ResourceKey<DamageType>> damageType) {
            this.base = base;
            this.modifier = modifier;
            this.source = source;
            this.damageType = damageType;
        }

        public float base() {
            return base;
        }

        public Optional<Modifier> modifier() {
            return modifier;
        }

        public Optional<DamageSourceDescription> source() {
            return source;
        }

        public Optional<ResourceKey<DamageType>> damageType() {
            return damageType;
        }

        public static class Modifier {
            public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    SerializableDataTypes.IDENTIFIER.fieldOf("attribute").forGetter(Modifier::attribute),
                    SerializableDataTypes.MODIFIER_OPERATION.fieldOf("operation").forGetter(Modifier::operation),
                    Codec.FLOAT.fieldOf("value").forGetter(Modifier::value)
            ).apply(instance, Modifier::new));

            private final ResourceLocation attribute;
            private final AttributeModifier.Operation operation;
            private final float value;

            public Modifier(ResourceLocation attribute, AttributeModifier.Operation operation, float value) {
                this.attribute = attribute;
                this.operation = operation;
                this.value = value;
            }

            public ResourceLocation attribute() {
                return attribute;
            }

            public AttributeModifier.Operation operation() {
                return operation;
            }

            public float value() {
                return value;
            }
        }
    }
}