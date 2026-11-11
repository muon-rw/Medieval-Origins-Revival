package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class IsCursedCondition implements ConditionType<ItemCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemEnchantments enchantments = ctx.stack().getEnchantments();
        if (enchantments == ItemEnchantments.EMPTY) {
            return false;
        }

        return enchantments.keySet().stream()
                .filter(holder -> enchantments.getLevel(holder) > 0)
                .anyMatch(holder -> holder.is(EnchantmentTags.CURSE));
    }
}