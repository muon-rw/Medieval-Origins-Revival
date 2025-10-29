package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.platform.Services;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class ModPowers {
    public static void register() {
        register(OwnerAttributeTransferPower.createFactory());
        registerPowerFactory(PixieWingsPower.PIXIE_WINGS_FACTORY);
        if (Services.PLATFORM.isModLoaded("icarus")) {
            registerPowerFactory(IcarusWingsPower.ICARUS_WINGS_FACTORY);
        }

    }

    public static void registerPowerFactory(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }

    private static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
