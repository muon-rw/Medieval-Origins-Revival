package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.muon.medievalorigins.util.ItemDataUtil;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public final class IsMeleeWeaponCondition implements ConditionType<ItemCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemStack stack = ctx.stack();
        boolean acceptsSharpness = ItemDataUtil.getEnchantment(ctx.level(), Enchantments.SHARPNESS)
                .canEnchant(stack);

        boolean hasAttackDamage = false;
        if (stack.getItem() instanceof DiggerItem) {
            var modifiers = ItemDataUtil.getAttributeModifiers(stack);
            final double[] totalDamage = {0.0};

            modifiers.forEach(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
                if (attribute.value() == Attributes.ATTACK_DAMAGE) {
                    totalDamage[0] += modifier.amount();
                }
            });

            hasAttackDamage = totalDamage[0] > 0;
        }

        return acceptsSharpness || hasAttackDamage;
    }
}