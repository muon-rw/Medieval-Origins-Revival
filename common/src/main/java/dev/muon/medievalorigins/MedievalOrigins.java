package dev.muon.medievalorigins;

import dev.muon.medievalorigins.action.ModBientityActions;
import dev.muon.medievalorigins.action.ModEntityActions;
import dev.muon.medievalorigins.attribute.ModAttributes;
import dev.muon.medievalorigins.condition.ModBientityConditions;
import dev.muon.medievalorigins.condition.ModEntityConditions;
import dev.muon.medievalorigins.condition.ModItemConditions;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.platform.Services;
import dev.muon.medievalorigins.power.ModPowers;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class MedievalOrigins {

    public static final String MOD_ID = "medievalorigins";
    public static final String MOD_NAME = "Medieval Origins Revival";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static ResourceLocation loc(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        LOG.info("Registering {} {} for {}", MOD_NAME, Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());

        ModAttributes.register();
        ModEnchantments.register();
        ModEntityActions.register();
        ModBientityActions.register();
        ModItemConditions.register();
        ModEntityConditions.register();
        ModBientityConditions.register();
        ModPowers.register();
    }
}