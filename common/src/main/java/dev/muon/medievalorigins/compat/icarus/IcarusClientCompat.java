package dev.muon.medievalorigins.compat.icarus;

import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.power.IcarusWingsPower;
import dev.muon.medievalorigins.power.PixieWingsPower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Shared compatibility logic for Icarus client-side functionality.
 * These methods have to be referenced in loader-specific mixins due to remapping quirks
 */
public class IcarusClientCompat {

    /**
     * Modifies the armor modifier to account for the Featherweight enchantment.
     * This allows armor with Featherweight to not slow down flight.
     */
    public static float modifyArmorModifier(float modifier, Player player) {
        var cfg = IcarusHelper.getConfigValues(player);
        int armorValueSum = 0;
        Iterable<ItemStack> armorItems = player.getArmorSlots();
        for (ItemStack armorItem : armorItems) {
            if (armorItem.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FEATHERWEIGHT, armorItem) > 0) {
                continue;
            }
            if (armorItem.getItem() instanceof ArmorItem armor) {
                armorValueSum += armor.getDefense();
            }
        }
        return cfg.armorSlows() ? Math.max(1.0F, armorValueSum / 20.0F * cfg.maxSlowedMultiplier()) : 1.0F;
    }

    /**
     * Handles rendering of origin-specific wings.
     * Returns the appropriate wing item stack based on the entity's powers.
     */
    public static ItemStack renderOriginWings(ItemStack original, LivingEntity entity) {
        if (original.isEmpty()) {
            ItemStack wingsType = IcarusWingsPower.getWingsType(entity);
            if (!wingsType.isEmpty()) {
                return wingsType;
            }
        } else if (PixieWingsPower.hasPower(entity)) {
            return new ItemStack(Items.AIR);
        }
        return original;
    }
}

