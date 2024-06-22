package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PixieWingsPower extends Power {

    public PixieWingsPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static boolean hasPower(Entity entity) {
        return true;
        //PowerHolderComponent.hasPower(entity, PixieWingsPower.class);
    }
    public static final PowerFactory<Power> PIXIE_WINGS_FACTORY = new PowerFactory<>(
            MedievalOrigins.loc("pixie_wings"),
            new SerializableData(),
            data -> (type, entity) -> new PixieWingsPower(type, entity)
    ).allowCondition();
}