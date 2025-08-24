package dev.muon.medievalorigins.condition.item;

import dev.muon.medievalorigins.condition.ModItemConditionTypes;
import dev.muon.medievalorigins.util.ItemDataUtil;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.ItemConditionContext;
import io.github.apace100.apoli.condition.type.ItemConditionType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IsValkyrieWeaponConditionType extends ItemConditionType {


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
    public boolean test(ItemConditionContext context) {
        ItemStack stack = context.stack();
        Level world = context.world();
        String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        boolean isWeapon = (stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof TridentItem ||
                ItemDataUtil.getEnchantment(world, Enchantments.SHARPNESS).canEnchant(stack) ||
                ItemDataUtil.getEnchantment(world, Enchantments.PIERCING).canEnchant(stack));

        if (isWeapon) {
            for (String keyword : BASE_VALKYRIE_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
        }

        if (isWeapon && FabricLoader.getInstance().isModLoaded("magistuarmory")) {
            for (String keyword : EPIC_KNIGHTS_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ModItemConditionTypes.IS_VALKYRIE_WEAPON;
    }
}