package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PreventCropTramplePowerType extends PowerType {

    public PreventCropTramplePowerType(Optional<EntityCondition> condition) {
        super(condition);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        // This will refer to the registration in ModPowerTypes
        return ModPowerTypes.PREVENT_CROP_TRAMPLE;
    }

    public static final PowerConfiguration<PreventCropTramplePowerType> FACTORY = PowerConfiguration.conditionedSimple(
            MedievalOrigins.loc("prevent_crop_trample"),
            PreventCropTramplePowerType::new
    );
} 