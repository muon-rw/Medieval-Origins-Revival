package dev.muon.medievalorigins.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class ItemDataUtil {
    /**
     * Gets an enchantment from the registry
     */
    public static Enchantment getEnchantment(Level world, ResourceKey<Enchantment> enchantmentKey) {
        return world.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(enchantmentKey)
                .value();
    }


    /**
     * Gets the level of a specific enchantment on an item
     */
    public static int getEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> enchantmentKey, Level world) {
        ItemEnchantments enchantments = stack.getEnchantments();
        Holder<Enchantment> holder = world.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(enchantmentKey);
        return enchantments.getLevel(holder);
    }

    /**
     * Gets the attribute modifiers on an item
     */
    public static ItemAttributeModifiers getAttributeModifiers(ItemStack stack) {
        return stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
    }
}