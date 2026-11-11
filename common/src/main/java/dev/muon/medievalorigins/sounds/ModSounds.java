package dev.muon.medievalorigins.sounds;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.List;

public final class ModSounds {
    // Powers reference these by id only, so we never need the holders.
    private static final List<String> SOUNDS = List.of(
            "banshee_cry", "channeled_wail", "horrible_screech", "zhh_woo_voop", "zhh_woo_voop_early",
            "cronch", "door_knock", "dash", "boost", "jump", "hover",
            "valkyrie_channel", "valkyrie_land", "divine_smite"
    );

    private ModSounds() {
    }

    public static void init() {
        for (String name : SOUNDS) {
            ResourceLocation id = MedievalOrigins.loc(name);
            Services.REGISTRAR.register(BuiltInRegistries.SOUND_EVENT, id, () -> SoundEvent.createVariableRangeEvent(id));
        }
    }
}
