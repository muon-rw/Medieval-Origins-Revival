package dev.muon.medievalorigins.client;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.client.render.SummonedArrowRenderer;
import dev.muon.medievalorigins.client.render.SummonedSkeletonRenderer;
import dev.muon.medievalorigins.client.render.SummonedWitherSkeletonRenderer;
import dev.muon.medievalorigins.client.render.SummonedZombieRenderer;
import dev.muon.medievalorigins.entity.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = MedievalOrigins.MOD_ID, dist = Dist.CLIENT)
public class MedievalOriginsNeoForgeClient {

    public MedievalOriginsNeoForgeClient(IEventBus modBus) {
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> {
            event.registerEntityRenderer(ModEntities.SUMMON_SKELETON.value(), SummonedSkeletonRenderer::new);
            event.registerEntityRenderer(ModEntities.SUMMON_ZOMBIE.value(), SummonedZombieRenderer::new);
            event.registerEntityRenderer(ModEntities.SUMMON_WITHER_SKELETON.value(), SummonedWitherSkeletonRenderer::new);
            event.registerEntityRenderer(ModEntities.SUMMONED_ARROW.value(), SummonedArrowRenderer::new);
        });
    }
}
