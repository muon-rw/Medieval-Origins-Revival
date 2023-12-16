package net.itsparkielad.medievalorigins.enchantments;

import net.itsparkielad.medievalorigins.MedievalOrigins;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;

public class ModEnchantments {

    public static final Enchantment FEATHERWEIGHT = new FeatherweightEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR);
    public static final Enchantment MIRRORING = new MirroringEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR_HEAD);
    public static void registerModEnchantments() {
        registerModEnchantments("featherweight", FEATHERWEIGHT);
        registerModEnchantments("mirroring", MIRRORING);
    }

    private static Enchantment registerModEnchantments(String path, Enchantment enchantment) {
        Registry.register(Registries.ENCHANTMENT, new Identifier(MedievalOrigins.MOD_ID, path), enchantment);
        return enchantment;
    }
}