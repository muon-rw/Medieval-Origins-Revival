package dev.muon.medievalorigins.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.entity.ISummon;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class OwnerAttributeTransferPower extends Power implements IDynamicFeatureConfiguration {
    private final List<AttributedEntityAttributeModifier> modifiers;
    private final int tickRate;
    private final boolean updateHealth;
    private final Attribute ownerAttribute;

    public OwnerAttributeTransferPower(PowerType<?> type, LivingEntity entity, int tickRate, boolean updateHealth, Attribute ownerAttribute, List<AttributedEntityAttributeModifier> modifiers) {
        super(type, entity);
        this.setTicking(true);
        this.tickRate = tickRate;
        this.updateHealth = updateHealth;
        this.ownerAttribute = ownerAttribute;
        this.modifiers = modifiers != null ? modifiers : new LinkedList<>();
    }

    @Override
    public void tick() {
        if (this.entity.tickCount % this.tickRate == 0) {
            if (this.isActive()) {
                this.applyModifiers();
            } else {
                this.removeModifiers();
            }
        }
    }

    @Override
    public void onRemoved() {
        this.removeModifiers();
    }

    private void applyModifiers() {
        if (entity instanceof ISummon) {
            double previousMaxHealth = entity.getMaxHealth();
            double previousHealthPercent = entity.getHealth() / previousMaxHealth;
            LivingEntity owner = ((ISummon) entity).getOwner();
            if (owner != null && ownerAttribute != null) {
                AttributeInstance ownerAttributeInstance = owner.getAttribute(ownerAttribute);
                if (ownerAttributeInstance != null) {
                    double ownerAttributeValue = ownerAttributeInstance.getValue();
                    modifiers.forEach(mod -> {
                        AttributeInstance summonAttributeInstance = entity.getAttribute(mod.getAttribute());
                        if (summonAttributeInstance != null) {
                            double finalModifierAmount = ownerAttributeValue * mod.getModifier().getAmount();
                            AttributeModifier newModifier = new AttributeModifier(
                                    mod.getModifier().getId(),
                                    mod.getModifier().getName(),
                                    finalModifierAmount,
                                    mod.getModifier().getOperation()
                            );
                            if (!summonAttributeInstance.hasModifier(newModifier)) {
                                summonAttributeInstance.addTransientModifier(newModifier);
                            }
                        }
                    });
                }
                if (updateHealth) {
                    double afterMaxHealth = entity.getMaxHealth();
                    entity.setHealth((float) (afterMaxHealth * previousHealthPercent));
                }
            }
        }
    }

    private void removeModifiers() {
        if (entity instanceof ISummon) {
            double previousMaxHealth = entity.getMaxHealth();
            double previousHealthPercent = entity.getHealth() / previousMaxHealth;
            modifiers.forEach(mod -> {
                AttributeInstance summonAttributeInstance = entity.getAttribute(mod.getAttribute());
                if (summonAttributeInstance != null) {
                    summonAttributeInstance.removeModifier(mod.getModifier());
                }
            });
            if (updateHealth) {
                double afterMaxHealth = entity.getMaxHealth();
                entity.setHealth((float) (afterMaxHealth * previousHealthPercent));
            }
        }
    }

    public static final Codec<OwnerAttributeTransferPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializableDataTypes.INT.fieldOf("tick_rate").forGetter(power -> power.tickRate),
            SerializableDataTypes.BOOLEAN.fieldOf("update_health").forGetter(power -> power.updateHealth),
            SerializableDataTypes.ATTRIBUTE.fieldOf("owner_attribute").forGetter(power -> power.ownerAttribute),
            ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS.listOf().optionalFieldOf("modifiers", new LinkedList<>()).forGetter(power -> Collections.singletonList(power.modifiers))
    ).apply(instance, (tickRate, updateHealth, ownerAttribute, modifiers) -> new OwnerAttributeTransferPower(null, null, tickRate, updateHealth, ownerAttribute, modifiers.get(0))));
}