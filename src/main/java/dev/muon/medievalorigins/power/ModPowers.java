package dev.muon.medievalorigins.power;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.PowerFactorySupplier;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class ModPowers {
        public static void register() {
            registerPowerFactory(BlazenbreathPower.getFactory());
        }
        public static void registerPowerFactory(PowerFactory<?> serializer) {
            Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
        }
        public static void registerPowerFactorySupplier(PowerFactorySupplier<?> supplier) {
            registerPowerFactory(supplier.createFactory());
        }
    }
