package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

public class CastFreelyPower extends Power {
    public CastFreelyPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("cast_freely"),
                new SerializableData(),
                data -> (powerType, livingEntity) -> new CastFreelyPower(powerType, livingEntity)
        ).allowCondition();
    }
}
