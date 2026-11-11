package dev.muon.medievalorigins;

import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonTracker;
import dev.muon.medievalorigins.platform.NeoForgeRegistrar;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(MedievalOrigins.MOD_ID)
public class MedievalOriginsNeoForge {

    public MedievalOriginsNeoForge(IEventBus modBus) {
        MedievalOrigins.init();
        NeoForgeRegistrar.attach(modBus);

        modBus.addListener(EntityAttributeCreationEvent.class,
                event -> ModEntities.createAttributes().forEach(event::put));

        NeoForge.EVENT_BUS.addListener(EntityJoinLevelEvent.class, event -> {
            if (!event.getLevel().isClientSide()) {
                SummonTracker.onEntityLoad(event.getEntity());
            }
        });
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> SummonTracker.onServerTickEnd());
        NeoForge.EVENT_BUS.addListener(LevelEvent.Unload.class, event -> {
            if (!event.getLevel().isClientSide()) {
                SummonTracker.onLevelUnload();
            }
        });
    }
}
