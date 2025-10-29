package dev.muon.medievalorigins.condition.item;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class IsValkyrieWeaponCondition {

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

    public static boolean condition(SerializableData.Instance data, ItemStack stack) {
        Item item = stack.getItem();
        String itemName = BuiltInRegistries.ITEM.getKey(item).getPath().toLowerCase(Locale.ROOT);

        boolean isWeapon = (item instanceof SwordItem ||
                item instanceof TridentItem ||
                Enchantments.SHARPNESS.canEnchant(stack) ||
                Enchantments.PIERCING.canEnchant(stack));

        if (isWeapon) {
            for (String keyword : BASE_VALKYRIE_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
        }

        if (Services.PLATFORM.isModLoaded("magistuarmory")) {
            for (String keyword : EPIC_KNIGHTS_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ConditionFactory<ItemStack> getFactory() {
        return new ConditionFactory<>(
                MedievalOrigins.loc("is_valkyrie_weapon"),
                new SerializableData(),
                IsValkyrieWeaponCondition::condition
        );
    }
} 