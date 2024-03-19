package dev.muon.medievalorigins;

import io.github.apace100.autotag.api.AutoTagRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.SwordItem;

import java.util.function.Predicate;

public class ModAutoTagRegistry {
    public static void registerTags() {
            register("weapons", item -> item instanceof BowItem || item instanceof DiggerItem || item instanceof SwordItem);
            register("bows", item -> item instanceof BowItem);
            register("daggers", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(dagger|sai|knife)[a-z_]*"));
            register("fist_weapons", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(fist|claw|gauntlet)[a-z_]*"));
            register("golden_armor", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*"));
            register("silver_armor", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*"));
            register("golden_weapons", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*"));
            register("golden_tools", item -> item instanceof DiggerItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*"));
    }


    public static void register(String tagName, Predicate<Item> condition) {
        ResourceLocation tagId = MedievalOrigins.loc(tagName);
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
        AutoTagRegistry.register(Registries.ITEM, tagKey, condition);
    }
}