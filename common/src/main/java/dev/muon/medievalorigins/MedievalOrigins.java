package dev.muon.medievalorigins;

import com.mojang.logging.LogUtils;
import dev.muon.medievalorigins.action.ModActions;
import dev.muon.medievalorigins.attribute.ModAttributes;
import dev.muon.medievalorigins.condition.ModConditions;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.item.ModItems;
import dev.muon.medievalorigins.power.ModPowers;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class MedievalOrigins {
    public static final String MOD_ID = "medievalorigins";
    public static final Logger LOGGER = LogUtils.getLogger();

    private MedievalOrigins() {
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /** Registers all content. Each loader calls this once from its mod entrypoint. */
    public static void init() {
        LOGGER.info("Loading Medieval Origins");

        ModAttributes.init();
        ModEntities.init();
        ModSounds.init();
        ModItems.init();

        ModActions.register();
        ModConditions.register();
        ModPowers.register();
    }
}
