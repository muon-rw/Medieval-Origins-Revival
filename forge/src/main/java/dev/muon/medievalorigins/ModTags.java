package dev.muon.medievalorigins;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;

import java.util.function.Predicate;

public class ModTags {
    public static void registerTags() {
        addToTag("summon_weapons", item -> item == Items.BOW || item instanceof DiggerItem || item instanceof SwordItem);
        addToTag("bows", item -> item instanceof BowItem);

        addToTag("axes", item ->
                item instanceof AxeItem ||
                        (item instanceof TieredItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("^(?!.*pickaxe).*axe.*$"))
        );

        addToTag("daggers", item ->
                item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(dagger|sai|knife)[a-z_]*") ||
                        item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).toString().matches("simplyswords:.*[a-z_]*(cutlass)[a-z_]*")
        );

        addToTag("fist_weapons", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(fist|claw|gauntlet)[a-z_]*"));

        addToTag("silver_armor", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*"));
        addToTag("silver_weapons", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*"));
        addToTag("silver_tools", item -> item instanceof DiggerItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*"));

        addToTag("golden_armor", item -> item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a.z_]*"));
        addToTag("golden_weapons", item -> item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a.z_]*(gold|gilded)[a.z_]*"));
        addToTag("golden_tools", item -> item instanceof DiggerItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a.z_]*(gold|gilded)[a.z_]*"));
    }

    public static TagKey<Item> bowsTag = TagKey.create(Registries.ITEM, MedievalOrigins.loc("bows"));

    public static void addToTag(String tagName, Predicate<Item> condition) {
    }
}