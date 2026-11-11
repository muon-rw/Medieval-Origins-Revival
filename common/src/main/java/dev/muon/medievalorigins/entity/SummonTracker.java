package dev.muon.medievalorigins.entity;

import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SummonTracker {
    private static final Map<UUID, Set<SummonedMob>> OWNER_TO_SUMMONS = new ConcurrentHashMap<>();
    private static int cleanupTicks = 0;
    private static final int CLEANUP_INTERVAL = 200;

    public static void onEntityLoad(Entity entity) {
        if (entity instanceof SummonedMob summon) {
            trackSummon(summon);
        }
    }

    public static void onServerTickEnd() {
        if (++cleanupTicks >= CLEANUP_INTERVAL) {
            cleanupTicks = 0;
            cleanupInvalidSummons();
        }
    }

    public static void onLevelUnload() {
        OWNER_TO_SUMMONS.clear();
    }

    public static void trackSummon(SummonedMob summon) {
        UUID ownerID = summon.getOwnerUUID();
        if (ownerID != null) {
            OWNER_TO_SUMMONS.computeIfAbsent(ownerID, k -> ConcurrentHashMap.newKeySet())
                    .add(summon);
        }
    }

    public static void untrackSummon(SummonedMob summon) {
        UUID ownerID = summon.getOwnerUUID();
        if (ownerID != null) {
            Set<SummonedMob> summons = OWNER_TO_SUMMONS.get(ownerID);
            if (summons != null) {
                summons.remove(summon);
                if (summons.isEmpty()) {
                    OWNER_TO_SUMMONS.remove(ownerID);
                }
            }
        }
    }

    public static Collection<SummonedMob> getSummonsForOwner(UUID ownerID) {
        return OWNER_TO_SUMMONS.getOrDefault(ownerID, Collections.emptySet());
    }

    public static void cleanupInvalidSummons() {
        OWNER_TO_SUMMONS.values().forEach(summons ->
                summons.removeIf(summon ->
                        !summon.getSelfAsMob().isAlive() ||
                                summon.getOwnerUUID() == null
                )
        );
    }
}