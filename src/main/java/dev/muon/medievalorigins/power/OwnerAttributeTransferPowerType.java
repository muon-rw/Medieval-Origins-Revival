package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.SummonedMob;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.AttributeModifying;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OwnerAttributeTransferPowerType extends PowerType implements AttributeModifying {
    private final Attribute attribute;
    private final Holder<Attribute> cachedHolder;
    private final List<AttributedEntityAttributeModifier> baseModifiers;
    private List<AttributedEntityAttributeModifier> scaledModifiers;
    private final boolean updateHealth;
    private final int tickRate;

    private Integer startTicks = null;
    private Integer endTicks = null;
    private boolean wasActive = false;
    private double lastOwnerValue = 0.0;


    public OwnerAttributeTransferPowerType(Attribute ownerAttribute, List<AttributedEntityAttributeModifier> attributedModifiers, boolean updateHealth, int tickRate, Optional<EntityCondition> condition) {
        super(condition);
        this.attribute = ownerAttribute;
        this.cachedHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(ownerAttribute);
        this.baseModifiers = attributedModifiers;
        this.scaledModifiers = new ArrayList<>(attributedModifiers);
        this.updateHealth = updateHealth;
        this.tickRate = tickRate;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return ModPowerTypes.OWNER_ATTRIBUTE_TRANSFER;
    }


    @Override
    public boolean shouldUpdateHealth() {
        return updateHealth;
    }

    private void updateScaledModifiersIfNeeded(double newOwnerValue) {
        if (lastOwnerValue != newOwnerValue) {
            scaledModifiers = baseModifiers.stream()
                    .map(mod -> new AttributedEntityAttributeModifier(
                            mod.attribute(),
                            new AttributeModifier(
                                    mod.modifier().id(),
                                    mod.modifier().amount() * newOwnerValue,
                                    mod.modifier().operation()
                            )
                    ))
                    .toList();
            lastOwnerValue = newOwnerValue;
        }
    }

    @Override
    public void serverTick() {
        if (!(getHolder() instanceof SummonedMob summon)) return;
        LivingEntity owner = summon.getOwner();
        if (owner == null) return;

        if (isActive()) {
            if (startTicks == null) {
                startTicks = getHolder().tickCount % tickRate;
                endTicks = null;
            }
            else if (!wasActive && getHolder().tickCount % tickRate == startTicks) {
                var ownerAttr = owner.getAttribute(cachedHolder);
                if (ownerAttr != null) {
                    updateScaledModifiersIfNeeded(ownerAttr.getValue());
                    addTemporaryModifiers(getHolder());
                    this.wasActive = true;
                }
            }
        }
        else if (wasActive) {
            if (endTicks == null) {
                startTicks = null;
                endTicks = getHolder().tickCount % tickRate;
            }
            else if (getHolder().tickCount % tickRate == endTicks) {
                removeModifiers(getHolder());
                this.wasActive = false;
            }
        }
    }

    @Override
    public List<AttributedEntityAttributeModifier> attributedModifiers() {
        return scaledModifiers;
    }

    @Override
    public void onAdded() {
        if (getHolder() instanceof SummonedMob summon && summon.getOwner() != null) {
            var ownerAttr = summon.getOwner().getAttribute(cachedHolder);
            if (ownerAttr != null) {
                updateScaledModifiersIfNeeded(ownerAttr.getValue());
                addTemporaryModifiers(getHolder());
                this.wasActive = true;
            }
        }
    }

    @Override
    public void onRemoved() {
        removeModifiers(getHolder());
    }

    @Override
    public void onLost() {
        removeModifiers(getHolder());
    }



    public static final PowerConfiguration<OwnerAttributeTransferPowerType> FACTORY = PowerConfiguration.conditionedOf(
            MedievalOrigins.loc("owner_attribute_transfer"),
            new SerializableData()
                    .add("owner_attribute", SerializableDataTypes.ATTRIBUTE)
                    .add("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS)
                    .add("update_health", SerializableDataTypes.BOOLEAN, true)
                    .add("tick_rate", SerializableDataTypes.POSITIVE_INT, 20),
            (data, condition) -> new OwnerAttributeTransferPowerType(
                    data.get("owner_attribute"),
                    data.get("modifiers"),
                    data.get("update_health"),
                    data.get("tick_rate"),
                    condition
            ),
            (type, data) -> data.instance()
                    .set("owner_attribute", type.attribute)
                    .set("modifiers", type.baseModifiers)
                    .set("update_health", type.updateHealth)
                    .set("tick_rate", type.tickRate)
    );
}