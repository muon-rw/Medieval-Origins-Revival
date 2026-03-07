package dev.muon.medievalorigins.item;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;

public class ModItems {
    public static final Item ALFIQ = register("alfiq", new Item(new Item.Properties()));
    public static final Item ARACHNAE = register("arachnae", new Item(new Item.Properties()));
    public static final Item BANSHEE = register("banshee", new Item(new Item.Properties()));
    public static final Item DWARF = register("dwarf", new Item(new Item.Properties()));
    public static final Item FAE = register("fae", new Item(new Item.Properties()));
    public static final Item GOBLIN = register("goblin", new Item(new Item.Properties()));
    public static final Item GORGON = register("gorgon", new Item(new Item.Properties()));
    public static final Item HIGH_ELF = register("high_elf", new Item(new Item.Properties()));
    public static final Item INCUBUS = register("incubus", new Item(new Item.Properties()));
    public static final Item KERES = register("keres", new Item(new Item.Properties()));
    public static final Item MOON_ELF = register("moon_elf", new Item(new Item.Properties()));
    public static final Item OGRE = register("ogre", new Item(new Item.Properties()));
    public static final Item PIXIE = register("pixie", new Item(new Item.Properties()));
    public static final Item PLAGUE_VICTIM = register("plague_victim", new Item(new Item.Properties()));
    public static final Item REVENANT = register("revenant", new Item(new Item.Properties()));
    public static final Item SIREN = register("siren", new Item(new Item.Properties()));
    public static final Item VALKYRIE = register("valkyrie", new Item(new Item.Properties()));
    public static final Item WOOD_ELF = register("wood_elf", new Item(new Item.Properties()));
    public static final Item YETI = register("yeti", new Item(new Item.Properties()));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, MedievalOrigins.loc(name), item);
    }

    public static void register() {
        MedievalOrigins.LOG.info("Registering Items for " + MedievalOrigins.MOD_NAME);
    }
}
