package dev.muon.medievalorigins.power;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.power.factory.IcarusWingsPowerFactory;
import dev.muon.medievalorigins.power.factory.OwnerAttributeTransferPowerFactory;
import dev.muon.medievalorigins.power.factory.PixieWingsPowerFactory;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPowers {
    public static final DeferredRegister<PowerFactory<?>> POWER_FACTORIES = DeferredRegister.create(ApoliRegistries.POWER_FACTORY_KEY, MedievalOrigins.MODID);
    public static final RegistryObject<PowerFactory<PixieWingsPower>> PIXIE_WINGS = POWER_FACTORIES.register("pixie_wings", PixieWingsPowerFactory::new);
    public static final RegistryObject<PowerFactory<IcarusWingsPower>> ICARUS_WINGS = POWER_FACTORIES.register("icarus_wings", IcarusWingsPowerFactory::new);
    public static final RegistryObject<PowerFactory<OwnerAttributeTransferPower>> OWNER_ATTRIBUTE_TRANSFER = POWER_FACTORIES.register("owner_attribute_transfer", OwnerAttributeTransferPowerFactory::new);

    public static void register(IEventBus eventBus) {
        POWER_FACTORIES.register(eventBus);
    }
}