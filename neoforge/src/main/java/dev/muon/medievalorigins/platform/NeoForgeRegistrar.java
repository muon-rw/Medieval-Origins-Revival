package dev.muon.medievalorigins.platform;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeRegistrar implements Registrar {
    private static final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> REGISTERS = new LinkedHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Holder<T> register(Registry<T> registry, ResourceLocation id, Supplier<? extends T> factory) {
        if (!id.getNamespace().equals(MedievalOrigins.MOD_ID)) {
            throw new IllegalArgumentException("Cannot register " + id + " outside our namespace");
        }
        DeferredRegister<T> register = (DeferredRegister<T>) REGISTERS.computeIfAbsent(registry.key(),
                key -> DeferredRegister.create((ResourceKey<? extends Registry<T>>) key, MedievalOrigins.MOD_ID));
        return register.register(id.getPath(), factory);
    }

    public static void attach(IEventBus modBus) {
        REGISTERS.values().forEach(register -> register.register(modBus));
    }
}
