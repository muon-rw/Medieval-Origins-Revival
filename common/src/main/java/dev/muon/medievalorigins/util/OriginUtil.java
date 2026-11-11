package dev.muon.medievalorigins.util;

import dev.overgrown.origins.component.PlayerOriginsAttachment;
import dev.overgrown.origins.component.PlayerOriginsImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class OriginUtil {
    private OriginUtil() {
    }

    public static boolean hasOrigin(Player player, ResourceLocation originId) {
        PlayerOriginsImpl origins = PlayerOriginsAttachment.get(player);
        return origins != null && origins.snapshot().containsValue(originId);
    }
}
