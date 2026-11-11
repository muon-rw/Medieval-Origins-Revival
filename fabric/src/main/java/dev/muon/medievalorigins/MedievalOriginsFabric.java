package dev.muon.medievalorigins;

import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonTracker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class MedievalOriginsFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		MedievalOrigins.init();

		ModEntities.createAttributes().forEach(FabricDefaultAttributeRegistry::register);

		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> SummonTracker.onEntityLoad(entity));
		ServerTickEvents.END_SERVER_TICK.register(server -> SummonTracker.onServerTickEnd());
		ServerWorldEvents.UNLOAD.register((server, level) -> SummonTracker.onLevelUnload());
	}
}
