package net.itsparkielad.medievalorigins;

import dev.cammiescorner.icarus.Icarus;
import dev.cammiescorner.icarus.core.util.WingsValues;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.itsparkielad.medievalorigins.enchantments.ModEnchantments;
import net.itsparkielad.medievalorigins.icarus.DefaultWingsValues;
import net.itsparkielad.medievalorigins.icarus.IcarusOrigins;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.function.Predicate;

public class MedievalOrigins implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("medievalorigins");
	public static final String MOD_ID = "medievalorigins";
	public static final Predicate<Entity> HAS_WINGS = FabricLoader.getInstance().isModLoaded("origins") ? IcarusOrigins::hasWings : entity -> false;
	public static final Function<Entity, WingsValues> WINGS = FabricLoader.getInstance().isModLoaded("origins") ? IcarusOrigins.getWingValues() : (entity) -> DefaultWingsValues.INSTANCE;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Loading Medieval Origins");
		ModEnchantments.registerModEnchantments();
		if(FabricLoader.getInstance().isModLoaded("icarus"))
			IcarusOrigins.register();
	}
}
