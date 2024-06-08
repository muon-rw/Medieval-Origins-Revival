package dev.muon.medievalorigins;

import com.bawnorton.mixinsquared.canceller.MixinCancellerRegistrar;
import dev.muon.medievalorigins.action.ModEntityActions;
import dev.muon.medievalorigins.action.ModBientityActions;
import dev.muon.medievalorigins.condition.ModEntityConditions;
import dev.muon.medievalorigins.condition.ModItemConditions;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.power.ModPowers;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MedievalOrigins implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("medievalorigins");

	// Unused, using custom conditions or registering to our tags with AutoTag API
	// TODO: use for legacy icon config
	/*
	public static void resourceOverrides() {
		ResourceLocation id = MedievalOrigins.loc("tag_loader");
		ModContainer container = getModContainer(id);
		ResourceManagerHelper.registerBuiltinResourcePack(id, container, ResourcePackActivationType.DEFAULT_ENABLED);
	}
	private static ModContainer getModContainer(ResourceLocation pack) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
				if (mod.findPath("resourcepacks/" + pack.getPath()).isPresent()) {
					LOGGER.info("LOADING DEV ENVIRONMENT DATAPACK");
					return mod;
				}
			}
		}
		return FabricLoader.getInstance().getModContainer(pack.getNamespace()).orElseThrow();
	}
	*/

	@Override
	public void onInitialize() {
		Constants.LOG.info("Loading Medieval Origins");

		CommonClass.init();


		ModEnchantments.register();
		ModEntities.register();
		ModEntityActions.register();
		ModBientityActions.register();
		ModItemConditions.register();
		ModEntityConditions.register();
		ModSounds.register();
		ModPowers.register();
		ModTags.registerTags();
	}
	static String MOD_ID = "medievalorigins";
	public static ResourceLocation loc(String id) {
		return new ResourceLocation(MOD_ID, id);
	}
}