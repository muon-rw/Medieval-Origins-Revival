package dev.muon.medievalorigins.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricRegistrar implements Registrar {

    @Override
    public <T> Holder<T> register(Registry<T> registry, ResourceLocation id, Supplier<? extends T> factory) {
        return Registry.registerForHolder(registry, id, factory.get());
    }
}
