package dev.muon.medievalorigins.compat;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamManager;
import net.minecraft.world.entity.Entity;

public class FTBTeamsUtils {

    public static boolean isAlliedFtbTeams(Entity actor, Entity target) {
        TeamManager manager = FTBTeamsAPI.api().getManager();
        if (manager.arePlayersInSameTeam(actor.getUUID(), target.getUUID())) {
            return true;
        }
        return false;
    }

}
