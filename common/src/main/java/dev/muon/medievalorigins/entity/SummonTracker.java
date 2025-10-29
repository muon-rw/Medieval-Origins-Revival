package dev.muon.medievalorigins.entity;

import net.minecraft.world.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SummonTracker {
    private static final Map<UUID, Set<ISummon>> OWNER_TO_SUMMONS = new ConcurrentHashMap<>();
    private static int cleanupTicks = 0;
    private static final int CLEANUP_INTERVAL = 200;

    /**
     * Called by platform-specific code when an entity loads
     */
    public static void onEntityLoad(ISummon summon) {
        trackSummon(summon);
    }

    /**
     * Called by platform-specific code each server tick
     */
    public static void onServerTick() {
        if (++cleanupTicks >= CLEANUP_INTERVAL) {
            cleanupTicks = 0;
            cleanupInvalidSummons();
        }
    }

    /**
     * Called by platform-specific code when a world unloads
     */
    public static void onWorldUnload() {
        OWNER_TO_SUMMONS.clear();
    }
    public static void trackSummon(ISummon summon) {
        UUID ownerID = summon.getOwnerUUID();
        if (ownerID != null) {
            OWNER_TO_SUMMONS.computeIfAbsent(ownerID, k -> ConcurrentHashMap.newKeySet())
                    .add(summon);
        }
    }

    public static void untrackSummon(ISummon summon) {
        UUID ownerID = summon.getOwnerUUID();
        if (ownerID != null) {
            Set<ISummon> summons = OWNER_TO_SUMMONS.get(ownerID);
            if (summons != null) {
                summons.remove(summon);
                if (summons.isEmpty()) {
                    OWNER_TO_SUMMONS.remove(ownerID);
                }
            }
        }
    }

    public static Collection<ISummon> getSummonsForOwner(UUID ownerID) {
        return OWNER_TO_SUMMONS.getOrDefault(ownerID, Collections.emptySet());
    }

    public static void cleanupInvalidSummons() {
        OWNER_TO_SUMMONS.values().forEach(summons ->
                summons.removeIf(summon -> {
                    LivingEntity entity = summon.getLivingEntity();
                    // Remove if: entity is null/dead OR owner UUID is null
                    return entity == null || !entity.isAlive() || summon.getOwnerUUID() == null;
                })
        );
    }
}