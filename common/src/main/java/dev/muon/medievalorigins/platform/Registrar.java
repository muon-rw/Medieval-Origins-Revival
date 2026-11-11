package dev.muon.medievalorigins.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface Registrar {

    /**
     * Registers an entry into a vanilla registry. On NeoForge the returned holder is unbound
     * until NeoForge fires the registry's {@code RegisterEvent}, so call {@link Holder#value()}
     * lazily.
     */
    <T> Holder<T> register(Registry<T> registry, ResourceLocation id, Supplier<? extends T> factory);
}
