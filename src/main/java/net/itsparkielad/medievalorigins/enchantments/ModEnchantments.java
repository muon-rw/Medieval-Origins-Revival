package net.itsparkielad.medievalorigins.enchantments;

import net.itsparkielad.medievalorigins.MedievalOrigins;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ModEnchantments {

    public static final Enchantment FEATHERWEIGHT = new FeatherweightEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});

    public static void registerModEnchantments() {
        registerModEnchantments("featherweight", FEATHERWEIGHT);
    }

    private static Enchantment registerModEnchantments(String path, Enchantment enchantment) {
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation(MedievalOrigins.MOD_ID, path), enchantment);
        return enchantment;
    }
}