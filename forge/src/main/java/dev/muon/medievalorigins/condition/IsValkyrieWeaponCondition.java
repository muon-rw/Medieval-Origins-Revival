package dev.muon.medievalorigins.condition;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemCondition;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class IsValkyrieWeaponCondition extends ItemCondition<NoConfiguration> {

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

    public IsValkyrieWeaponCondition() {
        super(NoConfiguration.CODEC);
    }

    @Override
    public boolean check(NoConfiguration configuration, @Nullable Level level, ItemStack stack) {
        Item item = stack.getItem();
        String itemName = ForgeRegistries.ITEMS.getKey(item).getPath().toLowerCase(Locale.ROOT);

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

        if (ModList.get().isLoaded("magistuarmory")) {
            for (String keyword : EPIC_KNIGHTS_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }
} 