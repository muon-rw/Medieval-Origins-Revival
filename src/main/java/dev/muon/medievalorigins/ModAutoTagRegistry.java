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
            addToTag("weapons", item -> item instanceof BowItem || item instanceof DiggerItem || item instanceof SwordItem);
            addToTag("bows", item -> item instanceof BowItem);
            addToTag("daggers", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(dagger|sai|knife)[a-z_]*"));
            addToTag("fist_weapons", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(fist|claw|gauntlet)[a-z_]*"));

            addToTag("silver_armor", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*"));
            addToTag("silver_weapons", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*"));
            addToTag("silver_tools", item -> item instanceof DiggerItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*"));

            addToTag("golden_armor", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*"));
            addToTag("golden_weapons", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*"));
            addToTag("golden_tools", item -> item instanceof DiggerItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*"));
    }

    public static void addToTag(String tagName, Predicate<Item> condition) {
        ResourceLocation tagId = MedievalOrigins.loc(tagName);
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
        AutoTagRegistry.register(Registries.ITEM, tagKey, condition);
    }
}