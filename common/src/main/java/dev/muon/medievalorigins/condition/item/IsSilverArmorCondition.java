package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public final class IsSilverArmorCondition implements ConditionType<ItemCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemStack stack = ctx.stack();
        String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        boolean isGoldArmor = stack.getItem() instanceof ArmorItem &&
                (itemName.contains("silver") || itemName.contains("iron"));

        boolean hasSilverTrim = false;
        // TODO: All the Trims compat
        /*
        ArmorTrim trim = stack.getComponents().get(DataComponents.TRIM);
        if (trim != null) {
            hasSilverTrim = trim.material().is(TrimMaterials.SILVER);
        }
        */
        return isGoldArmor || hasSilverTrim;
    }
}