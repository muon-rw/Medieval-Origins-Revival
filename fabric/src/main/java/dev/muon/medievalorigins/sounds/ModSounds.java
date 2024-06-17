package dev.muon.medievalorigins.sounds;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.Registry;

public class ModSounds {
    public static final SoundEvent BANSHEE_CRY = register("banshee_cry");
    public static final SoundEvent CHANNELED_WAIL = register("channeled_wail");
    public static final SoundEvent HORRIBLE_SCREECH = register("horrible_screech");
    public static final SoundEvent ZHH_WOO_VOOP = register("zhh_woo_voop");
    public static final SoundEvent ZHH_WOO_VOOP_EARLY = register("zhh_woo_voop_early");
    public static final SoundEvent CRONCH = register("cronch");
    public static final SoundEvent DOOR_KNOCK = register("door_knock");
    public static final SoundEvent DASH = register("dash");
    public static final SoundEvent BOOST = register("boost");
    public static final SoundEvent JUMP = register("jump");
    public static final SoundEvent HOVER = register("hover");

    private static SoundEvent register(String name) {
        ResourceLocation id = MedievalOrigins.loc(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
    public static void register() {
        MedievalOrigins.LOGGER.info("Registering Sounds for " + MedievalOrigins.LOGGER.getName());
    }
}