package net.itsparkielad.medievalorigins;

import net.fabricmc.api.ModInitializer;
import net.itsparkielad.medievalorigins.enchantments.ModEnchantments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class MedievalOrigins implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("medievalorigins");

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

	@Override
	public void onInitialize() {

		LOGGER.info("Loading Medieval Origins");
		ModEnchantments.registerModEnchantments();
		resourceOverrides();
	}

	public static ResourceLocation loc(String id) {
		return new ResourceLocation("medievalorigins", id);
	}

	public static final String MOD_ID = "medievalorigins";
}