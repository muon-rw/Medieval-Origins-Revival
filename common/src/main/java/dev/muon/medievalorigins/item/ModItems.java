package dev.muon.medievalorigins.item;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.List;

public final class ModItems {
    // Icon-only items for the origin selection screen
    private static final List<String> PORTRAITS = List.of(
            "alfiq", "arachnae", "banshee", "incubus", "dwarf",
            "wood_elf", "high_elf", "fae", "goblin", "gorgon",
            "moon_elf", "revenant", "ogre", "pixie",
            "plague_victim", "siren", "valkyrie", "yeti"
    );

    private ModItems() {
    }

    public static void init() {
        for (String origin : PORTRAITS) {
            Services.REGISTRAR.register(BuiltInRegistries.ITEM, MedievalOrigins.loc(origin), () -> new Item(new Item.Properties()));
        }
    }
}
