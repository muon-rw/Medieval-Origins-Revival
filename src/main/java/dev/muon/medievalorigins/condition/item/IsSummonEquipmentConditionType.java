package dev.muon.medievalorigins.condition.item;

import dev.muon.medievalorigins.condition.ModItemConditionTypes;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.ItemConditionContext;
import io.github.apace100.apoli.condition.type.ItemConditionType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class IsSummonEquipmentConditionType extends ItemConditionType {
    @Override
    public boolean test(ItemConditionContext context) {
        ItemStack stack = context.stack();
        Item item = stack.getItem();
        return item instanceof BowItem ||
                item instanceof DiggerItem ||
                item instanceof SwordItem ||
                item instanceof ArmorItem;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ModItemConditionTypes.IS_SUMMON_EQUIPMENT;
    }
}