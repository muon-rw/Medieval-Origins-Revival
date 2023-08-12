package net.itsparkielad.medievalorigins.enchantments;

import net.itsparkielad.medievalorigins.MedievalOrigins;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;

public class ModEnchantments {

    public static final Enchantment FEATHERWEIGHT = new FeatherweightEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});

    public static void registerModEnchantments() {
        registerModEnchantments("featherweight", FEATHERWEIGHT);
    }

    private static Enchantment registerModEnchantments(String path, Enchantment enchantment) {
        Registry.register(Registries.ENCHANTMENT, new Identifier(MedievalOrigins.MOD_ID, path), enchantment);
        return enchantment;
    }
}