package dev.muon.medievalorigins;

import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AutoTagRegistry {
    private static final List<AutoTag<?>> AUTO_TAGS = new ArrayList<>();

    public static <T> void registerAutoTag(AutoTag<T> autoTag) {
        AUTO_TAGS.add(autoTag);
    }

    public static List<AutoTag<?>> getAutoTags() {
        return AUTO_TAGS;
    }

    public static class AutoTag<T> {
        private final TagKey<T> tagKey;
        private final Predicate<T> predicate;
        private final TagKey<T> preventTagKey;

        public AutoTag(TagKey<T> tagKey, Predicate<T> predicate, TagKey<T> preventTagKey) {
            this.tagKey = tagKey;
            this.predicate = predicate;
            this.preventTagKey = preventTagKey;
        }

        public TagKey<T> tagKey() {
            return tagKey;
        }

        public Predicate<T> predicate() {
            return predicate;
        }

        public TagKey<T> preventTagKey() {
            return preventTagKey;
        }
    }
}