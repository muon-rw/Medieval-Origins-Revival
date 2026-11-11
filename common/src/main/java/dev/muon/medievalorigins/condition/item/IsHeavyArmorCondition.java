package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.util.ItemDataUtil;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public final class IsHeavyArmorCondition implements ConditionType<ItemCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemStack stack = ctx.stack();
        if (stack.getItem() instanceof ArmorItem armorItem) {
            int featherweightLevel = ItemDataUtil.getEnchantmentLevel(stack, ModEnchantments.FEATHERWEIGHT, ctx.level());
            return armorItem.getToughness() > 0 && featherweightLevel == 0;
        }
        return false;
    }
}