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
	public static final Logger LOGGER = LogManager.getLogger("medievalorigins");
	public static final String MOD_ID = "medievalorigins";
	/*
	public static final Predicate<Entity> HAS_WINGS = FabricLoader.getInstance().isModLoaded("origins") ? IcarusOrigins::hasWings : entity -> false;
	public static final Function<Entity, WingsValues> WINGS = FabricLoader.getInstance().isModLoaded("origins") ? IcarusOrigins.getWingValues() : (entity) -> DefaultWingsValues.INSTANCE;
    */
	@Override
	public void onInitialize() {
		LOGGER.info("Loading Medieval Origins");
		ModEnchantments.registerModEnchantments();
		// Unused, just using Trinkets equip for now
		// if(FabricLoader.getInstance().isModLoaded("icarus"))
		//	IcarusOrigins.register();
	}
}
