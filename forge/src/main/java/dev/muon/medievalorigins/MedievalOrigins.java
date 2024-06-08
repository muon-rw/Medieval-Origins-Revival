package dev.muon.medievalorigins;

import com.mojang.logging.LogUtils;
import dev.muon.medievalorigins.action.ModActions;
import dev.muon.medievalorigins.condition.ModConditions;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MedievalOrigins.MODID)
public class MedievalOrigins
{
    public static final String MODID = "medievalorigins";
    public static ResourceLocation loc(String id) {
        return new ResourceLocation(MODID, id);
    }
    private static final Logger LOGGER = LogUtils.getLogger();

    public MedievalOrigins()
    {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Loading Medieval Origins");
        CommonClass.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        ModEnchantments.register(modEventBus);
        ModEntities.register(modEventBus);
        ModActions.register(modEventBus);
        ModConditions.register(modEventBus);
        ModSounds.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {}
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {}

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    @OnlyIn(Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {}
    }
}
