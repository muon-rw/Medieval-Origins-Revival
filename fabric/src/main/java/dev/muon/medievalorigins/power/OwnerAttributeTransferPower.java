package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ISummon;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;

import java.util.LinkedList;
import java.util.List;

public class OwnerAttributeTransferPower extends Power {
    private final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<>();
    private final int tickRate;
    private final boolean updateHealth;
    private Attribute ownerAttribute;

    public OwnerAttributeTransferPower(PowerType<?> type, LivingEntity entity, int tickRate, boolean updateHealth) {
        super(type, entity);
        this.setTicking(true);
        this.tickRate = tickRate;
        this.updateHealth = updateHealth;
    }

    public void setOwnerAttribute(Attribute ownerAttribute) {
        this.ownerAttribute = ownerAttribute;
    }

    public OwnerAttributeTransferPower addModifier(AttributedEntityAttributeModifier modifier) {
        this.modifiers.add(modifier);
        return this;
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


    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("owner_attribute_transfer"),
                new SerializableData()
                        .add("owner_attribute", SerializableDataTypes.ATTRIBUTE)
                        .add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
                        .add("tick_rate", SerializableDataTypes.INT, 20)
                        .add("update_health", SerializableDataTypes.BOOLEAN, true),
                data -> (type, entity) -> {
                    OwnerAttributeTransferPower power = new OwnerAttributeTransferPower(type, entity, data.getInt("tick_rate"), data.getBoolean("update_health"));
                    power.setOwnerAttribute(data.get("owner_attribute"));
                    if (data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if (data.isPresent("modifiers")) {
                        ((List<AttributedEntityAttributeModifier>) data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                }
        ).allowCondition();
    }
}