package dev.muon.medievalorigins.enchantment;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> FEATHERWEIGHT = key("featherweight");
    public static final ResourceKey<Enchantment> MIRRORING = key("mirroring");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, MedievalOrigins.loc(name));
    }
}