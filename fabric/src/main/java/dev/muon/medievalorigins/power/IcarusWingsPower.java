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
import net.minecraft.world.item.ItemStack;

public class IcarusWingsPower extends Power {
    private final ItemStack wingsType;

    public IcarusWingsPower(PowerType<?> type, LivingEntity entity, ItemStack wingsType) {
        super(type, entity);
        this.wingsType = wingsType;
    }
    public ItemStack getWingsType() {
        return wingsType;
    }
    @Override
    public void onGained() {
    }

    @Override
    public void onRemoved() {
    }

    @Override
    public void onRespawn() {
    }

    public static boolean hasPower(Entity entity) {
        return PowerHolderComponent.hasPower(entity, IcarusWingsPower.class);
    }

    public static final PowerFactory<Power> ICARUS_WINGS_FACTORY = new PowerFactory<>(
            MedievalOrigins.loc("icarus_wings"),
            new SerializableData()
                    .add("wings_type", SerializableDataTypes.ITEM_STACK),
            data -> (powerType, entity) -> new IcarusWingsPower(powerType, entity, data.get("wings_type"))
    ).allowCondition();
}