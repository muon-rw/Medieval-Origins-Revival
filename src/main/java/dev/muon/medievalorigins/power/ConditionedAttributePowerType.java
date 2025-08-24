package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ConditionedAttributePowerType extends AttributePowerType {
    protected final int tickRate;
    private Integer startTicks = null;
    private Integer endTicks = null;
    private boolean wasActive = false;

    public ConditionedAttributePowerType(List<AttributedEntityAttributeModifier> attributedModifiers, boolean updateHealth, int tickRate, Optional<EntityCondition> condition) {
        super(attributedModifiers, updateHealth, condition);
        this.tickRate = tickRate;
        this.setTicking(true);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return ModPowerTypes.CONDITIONED_ATTRIBUTE;
    }

    @Override
    public void serverTick() {
        if (this.isActive()) {
            if (this.startTicks == null) {
                this.startTicks = this.getHolder().tickCount % this.tickRate;
                this.endTicks = null;
            } else if (!this.wasActive && this.getHolder().tickCount % this.tickRate == this.startTicks) {
                this.addTemporaryModifiers(this.getHolder());
                this.wasActive = true;
            }
        } else if (this.wasActive) {
            if (this.endTicks == null) {
                this.startTicks = null;
                this.endTicks = this.getHolder().tickCount % this.tickRate;
            } else if (this.getHolder().tickCount % this.tickRate == this.endTicks) {
                this.removeModifiers(this.getHolder());
                this.wasActive = false;
            }
        }
    }

    public static final PowerConfiguration<ConditionedAttributePowerType> FACTORY = PowerConfiguration.conditionedOf(
            MedievalOrigins.loc("conditioned_attribute"),
            new SerializableData()
                    .add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
                    .addFunctionedDefault("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS,
                            data -> MiscUtil.singletonListOrNull(data.get("modifier")))
                    .add("update_health", SerializableDataTypes.BOOLEAN, true)
                    .add("tick_rate", SerializableDataTypes.POSITIVE_INT, 20)
                    .validate(MiscUtil.validateAnyFieldsPresent("modifier", "modifiers")),
            (data, condition) -> new ConditionedAttributePowerType(
                    data.get("modifiers"),
                    data.get("update_health"),
                    data.get("tick_rate"),
                    condition
            ),
            (type, data) -> data.instance()
                    .set("modifiers", type.attributedModifiers())
                    .set("update_health", type.shouldUpdateHealth())
                    .set("tick_rate", type.tickRate)
    );
}
