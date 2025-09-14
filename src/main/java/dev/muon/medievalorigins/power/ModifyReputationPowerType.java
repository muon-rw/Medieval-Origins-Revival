package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.ValueModifyingPowerType;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyReputationPowerType extends ValueModifyingPowerType {

    public ModifyReputationPowerType(List<Modifier> modifiers, Optional<EntityCondition> condition) {
        super(modifiers, condition);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return ModPowerTypes.MODIFY_REPUTATION;
    }

    public static final PowerConfiguration<ModifyReputationPowerType> FACTORY = PowerConfiguration.of(
            MedievalOrigins.loc("modify_reputation"),
            ValueModifyingPowerType.createConditionedModifyingRequiredDataFactory(
                    new SerializableData(),
                    (data, modifiers, entityCondition) -> new ModifyReputationPowerType(
                            modifiers,
                            entityCondition
                    ),
                    (powerType, serializableData) -> serializableData.instance()
            )
    );
} 