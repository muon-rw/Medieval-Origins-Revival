package dev.muon.medievalorigins;

import dev.muon.medievalorigins.entity.ISummon;
import dev.muon.medievalorigins.entity.SummonTracker;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MedievalOrigins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ISummon summon) {
            SummonTracker.onEntityLoad(summon);
        }
    }
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            SummonTracker.onServerTick();
        }
    }
    
    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            SummonTracker.onWorldUnload();
        }
    }
}
