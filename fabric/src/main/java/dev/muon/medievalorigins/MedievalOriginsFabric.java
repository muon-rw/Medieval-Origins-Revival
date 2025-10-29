package dev.muon.medievalorigins;

import dev.muon.medievalorigins.action.ModEntityActions;
import dev.muon.medievalorigins.action.ModBientityActions;
import dev.muon.medievalorigins.condition.ModBientityConditions;
import dev.muon.medievalorigins.condition.ModEntityConditions;
import dev.muon.medievalorigins.condition.ModItemConditions;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.power.ModPowers;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;
import dev.muon.medievalorigins.enchantment.ModEnchantments;

public class MedievalOriginsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		MedievalOrigins.LOG.info("Loading Medieval Origins");

		MedievalOrigins.init();

		ModEntities.register();
		ModSounds.register();

	}
}