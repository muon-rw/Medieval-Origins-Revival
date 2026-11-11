package dev.muon.medievalorigins.platform;

public interface PlatformHelper {

    /**
     * Returns whether the mod is present. Safe to call from a mixin config plugin, before the
     * loader has finished constructing mods.
     */
    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    boolean isClient();
}
