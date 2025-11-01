package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class FaeWingsPower extends Power {

    public FaeWingsPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static boolean hasPower(Entity entity) {
        return PowerHolderComponent.hasPower(entity, FaeWingsPower.class);
    }

    public static final PowerFactory<Power> FAE_WINGS_FACTORY = new PowerFactory<>(
            MedievalOrigins.loc("fae_wings"),
            new SerializableData(),
            data -> (type, entity) -> new FaeWingsPower(type, entity)
    ).allowCondition();
}

