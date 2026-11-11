package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public final class IsSilverWeaponCondition implements ConditionType<ItemCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemStack stack = ctx.stack();
        String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return stack.getItem() instanceof SwordItem &&
                (itemName.contains("silver") || itemName.contains("iron"));
    }
}