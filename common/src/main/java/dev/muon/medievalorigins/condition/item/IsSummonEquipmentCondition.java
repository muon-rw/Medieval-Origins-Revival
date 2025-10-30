package dev.muon.medievalorigins.condition.item;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.item.*;
public class IsSummonEquipmentCondition {

    public static boolean condition(SerializableData.Instance data, ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof BowItem ||
                item instanceof DiggerItem ||
                item instanceof SwordItem ||
                item instanceof TridentItem ||
                item instanceof ArmorItem;
    }

    public static ConditionFactory<ItemStack> getFactory() {
        return new ConditionFactory<>(
                MedievalOrigins.loc("is_summon_equipment"),
                new SerializableData(),
                IsSummonEquipmentCondition::condition
        );
    }
}