package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.muon.medievalorigins.util.ItemDataUtil;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public final class IsToolCondition implements ConditionType<ItemCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemStack stack = ctx.stack();
        return stack.getItem() instanceof DiggerItem ||
                ItemDataUtil.getEnchantment(ctx.level(), Enchantments.EFFICIENCY).canEnchant(stack);
    }
}