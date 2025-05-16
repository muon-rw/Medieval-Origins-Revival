package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FaeWingsPowerType extends PowerType {

    public FaeWingsPowerType(Optional<EntityCondition> condition) {
        super(condition);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return ModPowerTypes.FAE_WINGS;
    }

    public static final PowerConfiguration<FaeWingsPowerType> FACTORY = PowerConfiguration.conditionedSimple(
            MedievalOrigins.loc("fae_wings"),
            FaeWingsPowerType::new
    );
} 