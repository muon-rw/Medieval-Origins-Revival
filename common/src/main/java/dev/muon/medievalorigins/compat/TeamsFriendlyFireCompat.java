package dev.muon.medievalorigins.compat;

import dev.muon.teamsfriendlyfire.compat.FTBTeamsUtils;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * Holder for Teams Friendly Fire reference, hiding ref to there in its own class for classloading safety
 * Callers must gate on {@code isModLoaded("teamsfriendlyfire")}.
 */
public final class TeamsFriendlyFireCompat {

    private TeamsFriendlyFireCompat() {
    }

    public static boolean arePlayersProtectedAllies(UUID playerId1, UUID playerId2, Level level) {
        return FTBTeamsUtils.arePlayersProtectedAllies(playerId1, playerId2, level);
    }
}
