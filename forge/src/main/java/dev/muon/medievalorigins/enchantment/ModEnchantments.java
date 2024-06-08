package dev.muon.medievalorigins.enchantment;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MedievalOrigins.MODID);
    public static final RegistryObject<Enchantment> FEATHERWEIGHT = ENCHANTMENTS.register("featherweight",
            () -> new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                @Override
                public int getMaxLevel() {
                    return 1;
                }
                public int getMinLevel() {
                    return 1;
                }
            });

    public static final RegistryObject<Enchantment> MIRRORING = ENCHANTMENTS.register("mirroring",
            () -> new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD}) {
                @Override
                public int getMaxLevel() {
                    return 1;
                }
                public int getMinLevel() {
                    return 1;
                }
            });
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }

}
