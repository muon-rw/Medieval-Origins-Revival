package dev.muon.medievalorigins.item;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MedievalOrigins.MOD_ID);

    public static final RegistryObject<Item> ALFIQ = register("alfiq");
    public static final RegistryObject<Item> ARACHNAE = register("arachnae");
    public static final RegistryObject<Item> BANSHEE = register("banshee");
    public static final RegistryObject<Item> DWARF = register("dwarf");
    public static final RegistryObject<Item> FAE = register("fae");
    public static final RegistryObject<Item> GOBLIN = register("goblin");
    public static final RegistryObject<Item> GORGON = register("gorgon");
    public static final RegistryObject<Item> HIGH_ELF = register("high_elf");
    public static final RegistryObject<Item> INCUBUS = register("incubus");
    public static final RegistryObject<Item> KERES = register("keres");
    public static final RegistryObject<Item> MOON_ELF = register("moon_elf");
    public static final RegistryObject<Item> OGRE = register("ogre");
    public static final RegistryObject<Item> PIXIE = register("pixie");
    public static final RegistryObject<Item> PLAGUE_VICTIM = register("plague_victim");
    public static final RegistryObject<Item> REVENANT = register("revenant");
    public static final RegistryObject<Item> SIREN = register("siren");
    public static final RegistryObject<Item> VALKYRIE = register("valkyrie");
    public static final RegistryObject<Item> WOOD_ELF = register("wood_elf");
    public static final RegistryObject<Item> YETI = register("yeti");

    private static RegistryObject<Item> register(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
