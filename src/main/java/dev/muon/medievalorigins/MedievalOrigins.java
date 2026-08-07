package dev.muon.medievalorigins;

import dev.muon.medievalorigins.action.ModEntityActionTypes;
import dev.muon.medievalorigins.action.ModBientityActionTypes;
import dev.muon.medievalorigins.attribute.ModAttributes;
import dev.muon.medievalorigins.condition.ModBientityConditionTypes;
import dev.muon.medievalorigins.condition.ModBlockConditionTypes;
import dev.muon.medievalorigins.condition.ModEntityConditionTypes;
import dev.muon.medievalorigins.condition.ModItemConditionTypes;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonTracker;
import dev.muon.medievalorigins.item.ModItems;
import dev.muon.medievalorigins.power.ModPowerTypes;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MedievalOrigins implements ModInitializer {
	public static String MOD_ID = "medievalorigins";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Medieval Origins");

		ModAttributes.register();
		ModEntities.register();
		ModSounds.register();
		ModItems.register();

		ModEntityActionTypes.register();
		ModBientityActionTypes.register();

		ModItemConditionTypes.register();
		ModBlockConditionTypes.register();
		ModEntityConditionTypes.register();
		ModBientityConditionTypes.register();

		ModPowerTypes.register();

		SummonTracker.init();
	}
}