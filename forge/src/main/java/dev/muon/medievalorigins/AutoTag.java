package dev.muon.medievalorigins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.Registry;

import java.util.HashMap;

public class AutoTag {
    private static final HashMap<Registry<?>, TagKey<?>> UNIVERSAL_PREVENT_TAGS_CACHE = new HashMap<>();
    public static final String MODID = "medievalorigins";

    @SuppressWarnings("unchecked")
    public static <T> TagKey<T> getUniversalPreventTag(Registry<T> registry) {
        return (TagKey<T>) UNIVERSAL_PREVENT_TAGS_CACHE.computeIfAbsent(registry, key ->
                TagKey.create(registry.key(), new ResourceLocation(MODID, "prevent/all"))
        );
    }
}