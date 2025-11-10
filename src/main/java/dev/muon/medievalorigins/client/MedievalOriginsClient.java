package dev.muon.medievalorigins.client;

import dev.muon.medievalorigins.client.render.SummonedArrowRenderer;
import dev.muon.medievalorigins.client.render.SummonedSkeletonRenderer;
import dev.muon.medievalorigins.client.render.SummonedWitherSkeletonRenderer;
import dev.muon.medievalorigins.client.render.SummonedZombieRenderer;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.util.PowerCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;


@Environment(EnvType.CLIENT)
public class MedievalOriginsClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("medievalorigins");

    public static final KeyMapping TERTIARY_ACTIVE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.medievalorigins.tertiary_active", // Translation key
            GLFW.GLFW_KEY_V,                       // Default key (V)
            "category.medievalorigins.general"      // Category translation key
    ));

    @Override
    public void onInitializeClient() {
        LOGGER.info("Loading Medieval Origins Client Resources");
        EntityRendererRegistry.register(ModEntities.SUMMON_SKELETON, SummonedSkeletonRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMON_ZOMBIE, SummonedZombieRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMON_WITHER_SKELETON, SummonedWitherSkeletonRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMONED_ARROW, SummonedArrowRenderer::new);
        
        initClientPowerCache();
    }
    
    private static void initClientPowerCache() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> PowerCache.tick());
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> PowerCache.invalidate(entity));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> PowerCache.clearAll());
    }
}