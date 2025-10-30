package dev.muon.medievalorigins.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.MedievalOriginsForge;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MedievalOrigins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ModEventBusEventsClient {
    public static final KeyMapping CANTRIP_ONE_KEY = new KeyMapping(
            "key.medievalorigins.tertiary_active",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.medievalorigins.general"
    );

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SUMMON_SKELETON.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.SUMMON_ZOMBIE.get(), ZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.SUMMON_WITHER_SKELETON.get(), WitherSkeletonRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(CANTRIP_ONE_KEY);
    }
}