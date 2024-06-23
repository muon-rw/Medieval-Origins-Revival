package dev.muon.medievalorigins.util;

public class SpellCastingUtil {
    private static final ThreadLocal<Boolean> requireAmmo = ThreadLocal.withInitial(() -> true);

    public static boolean requiresAmmo() {
        return requireAmmo.get();
    }

    public static void setRequireAmmo(boolean value) {
        requireAmmo.set(value);
    }

    private static final ThreadLocal<Boolean> bypassesCooldown = ThreadLocal.withInitial(() -> false);

    public static boolean bypassesCooldown() {
        return bypassesCooldown.get();
    }

    public static void setBypassesCooldown(boolean value) {
        bypassesCooldown.set(value);
    }
}