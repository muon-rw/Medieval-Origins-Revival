package dev.muon.medievalorigins.power;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.PowerFactorySupplier;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;

/**
 * I'm leaving it this way just to anger you
 */
public class ModPowers {
    public static void register() {
        register(OwnerAttributeTransferPower.createFactory());
        registerPowerFactory(PixieWingsPower.PIXIE_WINGS_FACTORY);
        if (FabricLoader.getInstance().isModLoaded("icarus")) {
            registerPowerFactory(IcarusWingsPower.ICARUS_WINGS_FACTORY);
        }

    }
    public static void registerPowerFactory(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
    public static void registerPowerFactorySupplier(PowerFactorySupplier<?> supplier) {
        registerPowerFactory(supplier.createFactory());
    }

    private static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }

    private static void register(PowerFactorySupplier<?> supplier) {
        register(supplier.createFactory());
    }

}
