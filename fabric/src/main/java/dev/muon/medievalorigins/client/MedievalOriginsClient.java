package dev.muon.medievalorigins.client;

import dev.muon.medievalorigins.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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
        EntityRendererRegistry.register(ModEntities.SUMMON_SKELETON, SkeletonRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMON_ZOMBIE, ZombieRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMON_WITHER_SKELETON, WitherSkeletonRenderer::new);
    }
}