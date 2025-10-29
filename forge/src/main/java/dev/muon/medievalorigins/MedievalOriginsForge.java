package dev.muon.medievalorigins;

import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MedievalOrigins.MOD_ID)
public class MedievalOriginsForge {

    public MedievalOriginsForge(FMLJavaModLoadingContext context, IEventBus modEventBus) {
        MedievalOrigins.LOG.info("Loading Medieval Origins");
        modEventBus.addListener(this::commonSetup);
        MedievalOrigins.init();

        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}
}