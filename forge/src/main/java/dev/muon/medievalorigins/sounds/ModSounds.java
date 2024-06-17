package dev.muon.medievalorigins.sounds;

import dev.muon.medievalorigins.MedievalOrigins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MedievalOrigins.MODID);
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
    public static RegistryObject<SoundEvent> BANSHEE_CRY = registerSoundEvents("banshee_cry");
    public static RegistryObject<SoundEvent> CHANNELED_WAIL = registerSoundEvents("channeled_wail");
    public static RegistryObject<SoundEvent> HORRIBLE_SCREECH = registerSoundEvents("horrible_screech");
    public static RegistryObject<SoundEvent> ZHH_WOO_VOOP = registerSoundEvents("zhh_woo_voop");
    public static RegistryObject<SoundEvent> ZHH_WOO_VOOP_EARLY = registerSoundEvents("zhh_woo_voop_early");
    public static RegistryObject<SoundEvent> CRONCH = registerSoundEvents("cronch");
    public static RegistryObject<SoundEvent> DOOR_KNOCK = registerSoundEvents("door_knock");
    public static RegistryObject<SoundEvent> DASH = registerSoundEvents("dash");
    public static RegistryObject<SoundEvent> BOOST = registerSoundEvents("boost");
    public static RegistryObject<SoundEvent> JUMP = registerSoundEvents("jump");
    public static RegistryObject<SoundEvent> HOVER = registerSoundEvents("hover");


    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MedievalOrigins.MODID, name)));
    }

}