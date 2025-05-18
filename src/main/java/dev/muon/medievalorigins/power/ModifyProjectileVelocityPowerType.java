package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.power.type.ValueModifyingPowerType;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyProjectileVelocityPowerType extends ValueModifyingPowerType {
    private final Optional<EntityAction> selfAction;

    public ModifyProjectileVelocityPowerType(List<Modifier> modifiers, Optional<EntityCondition> condition, Optional<EntityAction> selfAction) {
        super(modifiers, condition);
        this.selfAction = selfAction;
    }

    public void executeActions() {
        this.selfAction.ifPresent(action -> action.execute(this.getHolder()));
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return ModPowerTypes.MODIFY_PROJECTILE_VELOCITY;
    }

    public static final PowerConfiguration<ModifyProjectileVelocityPowerType> FACTORY = PowerConfiguration.of(
            MedievalOrigins.loc("modify_projectile_velocity"),
            ValueModifyingPowerType.createConditionedModifyingRequiredDataFactory(
                    new SerializableData()
                            .add("self_action", EntityAction.DATA_TYPE.optional(), Optional.empty()),
                    (data, modifiers, entityCondition) -> new ModifyProjectileVelocityPowerType(
                            modifiers,
                            entityCondition,
                            data.get("self_action")
                    ),
                    (powerType, serializableData) -> serializableData.instance()
                            .set("self_action", powerType.selfAction)
            )
    );
} 