package dev.muon.medievalorigins.condition.item;

import com.mojang.serialization.MapCodec;
import dev.muon.medievalorigins.platform.Services;
import dev.muon.medievalorigins.util.ItemDataUtil;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.ItemCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class IsValkyrieWeaponCondition implements ConditionType<ItemCtx, EmptyCfg> {

    private static final Set<String> BASE_VALKYRIE_KEYWORDS = new HashSet<>(Arrays.asList(
            "glaive", "spear", "lance", "trident", "halberd"
    ));

    private static final Set<String> EPIC_KNIGHTS_KEYWORDS = new HashSet<>(Arrays.asList(
            // Epic Knights
            "pike", "ranseur", "ahlspiess", "guisarme",
            // Epic Knights: Addon
            "poleaxe", "billhook", "fauchard", "partisan", "voulge",
            // Epic Knights: Slavic Armory
            "rogatina",
            // Epic Knights: Antique Legacy
            "doru", "retiarius", "sarissa"
    ));

    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, ItemCtx ctx) {
        ItemStack stack = ctx.stack();
        String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        boolean isWeapon = (stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof TridentItem ||
                ItemDataUtil.getEnchantment(ctx.level(), Enchantments.SHARPNESS).canEnchant(stack) ||
                ItemDataUtil.getEnchantment(ctx.level(), Enchantments.PIERCING).canEnchant(stack));

        if (isWeapon) {
            for (String keyword : BASE_VALKYRIE_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
        }

        if (isWeapon && Services.PLATFORM.isModLoaded("magistuarmory")) {
            for (String keyword : EPIC_KNIGHTS_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }
}