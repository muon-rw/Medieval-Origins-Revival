package dev.muon.medievalorigins.util;

import dev.muon.medievalorigins.compat.FTBTeamsUtils;
import dev.muon.medievalorigins.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class AllianceUtil {

    private static final int MAX_OWNERSHIP_DEPTH = 3; // Max depth for recursive owner search

    /**
     * Recursively gets the relevant Player UUID for team affiliation checks by traversing ownership.
     *
     * @param entity The entity to check.
     * @return The ultimate Player owner's UUID for affiliation, or null if not found within max depth.
     */
    public static UUID getPlayerAffiliationIdRecursive(Entity entity) {
        return getPlayerAffiliationIdRecursiveInternal(entity, MAX_OWNERSHIP_DEPTH);
    }

    private static UUID getPlayerAffiliationIdRecursiveInternal(Entity entity, int currentDepth) {
        if (entity == null || currentDepth <= 0) {
            return null;
        }

        if (entity instanceof Player player) {
            return player.getUUID();
        }

        if (entity instanceof OwnableEntity ownable) {
            LivingEntity owner = ownable.getOwner();
            if (owner instanceof Player ownerPlayer) {
                return ownerPlayer.getUUID();
            }
            // If owner is not a player, recurse on the owner
            return getPlayerAffiliationIdRecursiveInternal(owner, currentDepth - 1);
        }

        return null;
    }

    /**
     * Checks if two entities are allied through FTB Teams, based on their (potentially recursively resolved) affiliation IDs.
     *
     * @param affiliationId1 UUID of the first entity/owner.
     * @param affiliationId2 UUID of the second entity/owner.
     * @param world          The level/world the entities are in.
     * @return True if allied by FTB Teams, false otherwise or if FTB Teams is not available.
     */
    public static boolean areEntitiesAlliedByFTBTeams(UUID affiliationId1, UUID affiliationId2, Level world) {
        if (affiliationId1 == null || affiliationId2 == null) {
            return false;
        }

        if (affiliationId1.equals(affiliationId2)) {
            return true;
        }

        if (Services.PLATFORM.isModLoaded("ftbteams")) {
            return FTBTeamsUtils.arePlayersInSameTeam(affiliationId1, affiliationId2, world);
        }

        return false;
    }
}

