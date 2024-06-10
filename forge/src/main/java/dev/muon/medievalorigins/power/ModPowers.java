package dev.muon.medievalorigins.power;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPowers {
    public static final DeferredRegister<PowerFactory<?>> POWER_FACTORIES = DeferredRegister.create(ApoliRegistries.POWER_FACTORY_KEY, MedievalOrigins.MODID);
    public static final RegistryObject<PowerFactory<IcarusWingsPower>> ICARUS_WINGS = POWER_FACTORIES.register("icarus_wings", IcarusWingsPowerFactory::new);
    public static void register(IEventBus eventBus) {
        POWER_FACTORIES.register(eventBus);
    }
}