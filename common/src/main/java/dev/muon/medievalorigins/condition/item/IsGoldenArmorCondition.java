package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterials;

public final class IsGoldenArmorCondition implements ConditionType<ItemCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemStack stack = ctx.stack();
        String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        boolean isGoldArmor = stack.getItem() instanceof ArmorItem &&
                (itemName.contains("gold") || itemName.contains("gilded"));

        boolean hasGoldTrim = false;
        ArmorTrim trim = stack.getComponents().get(DataComponents.TRIM);
        if (trim != null) {
            hasGoldTrim = trim.material().is(TrimMaterials.GOLD);
        }

        return isGoldArmor || hasGoldTrim;
    }
}