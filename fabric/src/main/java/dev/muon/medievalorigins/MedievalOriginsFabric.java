package dev.muon.medievalorigins;

import dev.muon.medievalorigins.entity.ISummon;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonTracker;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class MedievalOriginsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		MedievalOrigins.LOG.info("Loading Medieval Origins");

		MedievalOrigins.init();
		ModEntities.register();
		ModSounds.register();
		
		registerSummonTrackerEvents();
	}
	
	private void registerSummonTrackerEvents() {
		// Track summons when they load
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof ISummon summon) {
				SummonTracker.onEntityLoad(summon);
			}
		});

		// Cleanup invalid summons periodically
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			SummonTracker.onServerTick();
		});

		// Clear tracker when world unloads
		ServerWorldEvents.UNLOAD.register((server, world) -> {
			SummonTracker.onWorldUnload();
		});
		
		MedievalOrigins.LOG.info("Registered SummonTracker events (Fabric)");
	}
}