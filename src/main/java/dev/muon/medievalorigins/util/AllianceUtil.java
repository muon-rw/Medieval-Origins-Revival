package dev.muon.medievalorigins.util;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import dev.muon.medievalorigins.compat.TeamsFriendlyFireCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class AllianceUtil {

    private static final int MAX_OWNERSHIP_DEPTH = 3; // Max depth for recursive owner search
    private static final boolean TEAMS_FRIENDLY_FIRE_LOADED = FabricLoader.getInstance().isModLoaded("teamsfriendlyfire");

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
     * Checks if two entities are allied, based on their (potentially recursively resolved) affiliation IDs.
     *
     * <p>Teams Friendly Fire answers this when installed, so team-configured PvP flags and reciprocity
     * apply. Otherwise this falls back to plain FTB Teams membership, which reports allied while the
     * team manager has not finished loading.
     *
     * @param affiliationId1 UUID of the first entity/owner.
     * @param affiliationId2 UUID of the second entity/owner.
     * @param world          The level/world the entities are in.
     * @return True if the two are allied, false otherwise.
     */
    public static boolean areEntitiesAlliedByFTBTeams(UUID affiliationId1, UUID affiliationId2, Level world) {
        if (affiliationId1 == null || affiliationId2 == null) {
            return false; 
        }
        if (affiliationId1.equals(affiliationId2)) {
            return true; 
        }

        if (TEAMS_FRIENDLY_FIRE_LOADED) {
            return TeamsFriendlyFireCompat.arePlayersProtectedAllies(affiliationId1, affiliationId2, world);
        }

        if (FabricLoader.getInstance().isModLoaded("ftbteams")) {
            boolean managerAvailable = world.isClientSide ?
                    FTBTeamsAPI.api().isClientManagerLoaded() :
                    FTBTeamsAPI.api().isManagerLoaded();

            if (!managerAvailable) {
                return true; 
            }

            if (world.isClientSide) {
                var clientManager = FTBTeamsAPI.api().getClientManager();
                Optional<KnownClientPlayer> player1Known = clientManager.getKnownPlayer(affiliationId1);
                Optional<KnownClientPlayer> player2Known = clientManager.getKnownPlayer(affiliationId2);

                return player1Known.isPresent() && player2Known.isPresent() && player1Known.get().teamId().equals(player2Known.get().teamId());
            } else {
                var serverManager = FTBTeamsAPI.api().getManager();
                return serverManager.arePlayersInSameTeam(affiliationId1, affiliationId2);
            }
        }
        return false; 
    }
} 