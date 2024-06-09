package dev.muon.medievalorigins;

import com.mojang.logging.LogUtils;
import dev.muon.medievalorigins.action.ModActions;
import dev.muon.medievalorigins.condition.ModConditions;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MedievalOrigins.MODID)
public class MedievalOrigins
{
    public static final String MODID = "medievalorigins";
    public static ResourceLocation loc(String id) {
        return new ResourceLocation(MODID, id);
    }
    private static final Logger LOGGER = LogUtils.getLogger();

    public MedievalOrigins()
    {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Loading Medieval Origins");
        CommonClass.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ModEnchantments.register(modEventBus);
        ModEntities.register(modEventBus);
        ModActions.register(modEventBus);
        ModConditions.register(modEventBus);
        ModSounds.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModTags.registerTags();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        modifyTags();
    }

    private void modifyTags() {
        List<AutoTagRegistry.AutoTag<?>> autoTags = AutoTagRegistry.getAutoTags();
        if (autoTags.isEmpty()) {
            return;
        }
        Registry<Item> registry = BuiltInRegistries.ITEM;
        Map<TagKey<Item>, List<Item>> tagEntries = getTagEntries(registry);

        Map<TagKey<Item>, List<Item>> returnMap = new HashMap<>(tagEntries);
        Set<TagKey<Item>> madeListMutable = new HashSet<>();

        TagKey<Item> universalPreventTag = AutoTag.getUniversalPreventTag(registry);

        registry.stream().forEach(entry -> {
            Item value = entry;
            for (AutoTagRegistry.AutoTag<?> autoTag : autoTags) {
                Predicate<Item> predicate = (Predicate<Item>) autoTag.predicate();

                TagKey<Item> preventTagKey = (TagKey<Item>) autoTag.preventTagKey();
                if (tagEntries.containsKey(preventTagKey) && tagEntries.get(preventTagKey).contains(entry)) {
                    continue;
                }

                if (tagEntries.containsKey(universalPreventTag) && tagEntries.get(universalPreventTag).contains(entry)) {
                    continue;
                }

                if (predicate.test(value)) {
                    TagKey<Item> tagKey = (TagKey<Item>) autoTag.tagKey();
                    List<Item> entryList;
                    if (returnMap.containsKey(tagKey)) {
                        entryList = returnMap.get(tagKey);
                        if (!madeListMutable.contains(tagKey)) {
                            entryList = new ArrayList<>(entryList);
                            returnMap.put(tagKey, entryList);
                            madeListMutable.add(tagKey);
                        }
                    } else {
                        entryList = new ArrayList<>();
                        returnMap.put(tagKey, entryList);
                        madeListMutable.add(tagKey);
                    }
                    entryList.add(entry);
                }
            }
        });
        autoTags.forEach(autoTag -> returnMap.remove(autoTag.preventTagKey()));
        returnMap.remove(universalPreventTag);

        applyModifiedTags(registry, returnMap);
    }

    private Map<TagKey<Item>, List<Item>> getTagEntries(Registry<Item> registry) {
        Map<TagKey<Item>, List<Item>> tagEntries = new HashMap<>();
        registry.getTagNames().forEach(tagKey -> {
            registry.getTag(tagKey).ifPresent(tag -> {
                tagEntries.put(tagKey, tag.stream().map(holder -> holder.value()).collect(Collectors.toList()));
            });
        });
        return tagEntries;
    }

    private void applyModifiedTags(Registry<Item> registry, Map<TagKey<Item>, List<Item>> modifiedTags) {
        modifiedTags.forEach((tagKey, items) -> {
            registry.getOrCreateTag(tagKey).bind(items.stream().map(registry::wrapAsHolder).collect(Collectors.toList()));
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}
    }
}