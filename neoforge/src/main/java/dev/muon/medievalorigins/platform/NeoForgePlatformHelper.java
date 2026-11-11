package dev.muon.medievalorigins.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;

public class NeoForgePlatformHelper implements PlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        // NeoForge builds ModList after mixin configs load, so our mixin plugin falls back to LoadingModList.
        ModList modList = ModList.get();
        return modList != null ? modList.isLoaded(modId) : LoadingModList.get().getModFileById(modId) != null;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }
}
