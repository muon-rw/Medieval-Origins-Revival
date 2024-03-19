package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

public class BlazenbreathPower extends Power {
    public BlazenbreathPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
    public static PowerFactory getFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("blazenbreath"),
                new SerializableData(),
                data -> (powerType, livingEntity) -> new BlazenbreathPower(powerType, livingEntity)
        ).allowCondition();
    }
}