package dev.muon.medievalorigins.enchantment;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ModEnchantments {

    public static final Enchantment FEATHERWEIGHT = new FeatherweightEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    public static final Enchantment MIRRORING = new MirroringEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD);

    public static void register() {
        register("featherweight", FEATHERWEIGHT);
        register("mirroring", MIRRORING);
    }
    private static Enchantment register(String path, Enchantment enchantment) {
        Registry.register(BuiltInRegistries.ENCHANTMENT, MedievalOrigins.loc(path), enchantment);
        return enchantment;
    }
}