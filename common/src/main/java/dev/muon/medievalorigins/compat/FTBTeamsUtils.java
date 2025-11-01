package dev.muon.medievalorigins.compat;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class FTBTeamsUtils {

    /**
     * Check if two players are in the same FTB Team, with client-side safety.
     *
     * @param playerId1 UUID of the first player
     * @param playerId2 UUID of the second player
     * @param world The level/world
     * @return true if they're on the same team, false otherwise
     */
    public static boolean arePlayersInSameTeam(UUID playerId1, UUID playerId2, Level world) {
        if (playerId1 == null || playerId2 == null) {
            return false;
        }

        if (playerId1.equals(playerId2)) {
            return true;
        }

        try {
            if (world.isClientSide()) {
                return checkClientTeamRelation(playerId1, playerId2);
            } else {
                return checkServerTeamRelation(playerId1, playerId2);
            }
        } catch (Exception e) {
            // If anything goes wrong, just let this one fail quietly
            return false;
        }
    }

    private static boolean checkClientTeamRelation(UUID playerId1, UUID playerId2) {
        if (!FTBTeamsAPI.api().isClientManagerLoaded()) {
            return false;
        }

        var manager = FTBTeamsAPI.api().getClientManager();
        Optional<KnownClientPlayer> player1Known = manager.getKnownPlayer(playerId1);
        Optional<KnownClientPlayer> player2Known = manager.getKnownPlayer(playerId2);

        if (player1Known.isEmpty() || player2Known.isEmpty()) {
            return false;
        }

        return player1Known.get().teamId().equals(player2Known.get().teamId());
    }

    private static boolean checkServerTeamRelation(UUID playerId1, UUID playerId2) {
        if (!FTBTeamsAPI.api().isManagerLoaded()) {
            return false;
        }

        var manager = FTBTeamsAPI.api().getManager();
        return manager.arePlayersInSameTeam(playerId1, playerId2);
    }
}

