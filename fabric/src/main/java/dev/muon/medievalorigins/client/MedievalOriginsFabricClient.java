package dev.muon.medievalorigins.client;

import dev.muon.medievalorigins.client.render.SummonedArrowRenderer;
import dev.muon.medievalorigins.client.render.SummonedSkeletonRenderer;
import dev.muon.medievalorigins.client.render.SummonedWitherSkeletonRenderer;
import dev.muon.medievalorigins.client.render.SummonedZombieRenderer;
import dev.muon.medievalorigins.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MedievalOriginsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.SUMMON_SKELETON.value(), SummonedSkeletonRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMON_ZOMBIE.value(), SummonedZombieRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMON_WITHER_SKELETON.value(), SummonedWitherSkeletonRenderer::new);
        EntityRendererRegistry.register(ModEntities.SUMMONED_ARROW.value(), SummonedArrowRenderer::new);
    }
}
