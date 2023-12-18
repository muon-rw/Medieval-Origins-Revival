package net.itsparkielad.medievalorigins.enchantments;

import net.itsparkielad.medievalorigins.MedievalOrigins;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ModEnchantments {

    public static final Enchantment FEATHERWEIGHT = new FeatherweightEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    public static final Enchantment MIRRORING = new MirroringEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD);

    public static void registerModEnchantments() {
        registerModEnchantments("featherweight", FEATHERWEIGHT);
        registerModEnchantments("mirroring", MIRRORING);
    }
    private static Enchantment registerModEnchantments(String path, Enchantment enchantment) {
        Registry.register(BuiltInRegistries.ENCHANTMENT, MedievalOrigins.loc(path), enchantment);
        return enchantment;
    }
}