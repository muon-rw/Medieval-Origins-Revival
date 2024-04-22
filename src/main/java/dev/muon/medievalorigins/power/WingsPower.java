package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class WingsPower extends Power {
    private final Item wingsType;
    private final float speed;
    private final float armorSlowMultiplier;
    private final boolean canFly;
    private final boolean armorSlows;

    public WingsPower(PowerType<?> type, LivingEntity entity, Item wingsType, float speed, float armorSlowMultiplier, boolean canFly, boolean armorSlows) {
        super(type, entity);
        this.wingsType = wingsType;
        this.speed = speed;
        this.armorSlowMultiplier = armorSlowMultiplier;
        this.canFly = canFly;
        this.armorSlows = armorSlows;
    }

    public void onGained() {

    }


    public void onRemoved() {

    }

    public void onRespawn() {

    }

    public static boolean hasWingsPower(Entity entity) {
        return PowerHolderComponent.hasPower(entity, WingsPower.class);
    }

    public static final PowerFactory<Power> WINGS_FACTORY = new PowerFactory<>(
            MedievalOrigins.loc("wings"),
            new SerializableData()
                    .add("wings_type", SerializableDataTypes.ITEM, null)
                    .add("speed", SerializableDataTypes.FLOAT, 0.02F)
                    .add("armor_slow_multiplier", SerializableDataTypes.FLOAT, 3F)
                    .add("can_fly", SerializableDataTypes.BOOLEAN, true)
                    .add("armor_slows", SerializableDataTypes.BOOLEAN, true),
            data -> (powerType, entity) -> new WingsPower(powerType, entity,
                    data.get("wings_type"),
                    data.getFloat("speed"),
                    data.getFloat("armor_slow_multiplier"),
                    data.getBoolean("can_fly"),
                    data.getBoolean("armor_slows"))
    ).allowCondition();
}