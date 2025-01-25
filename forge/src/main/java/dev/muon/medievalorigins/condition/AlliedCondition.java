package dev.muon.medievalorigins.condition;

import dev.ftb.mods.ftblibrary.FTBLibrary;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamManager;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityCondition;

import java.util.function.BiPredicate;

import io.github.edwinmindcraft.apoli.common.condition.bientity.SimpleBiEntityCondition;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;

public class AlliedCondition extends BiEntityCondition<NoConfiguration> {

    public static boolean isAllied(Entity actor, Entity target) {
        if (ModList.get().isLoaded("ftbteams")) {
            TeamManager manager = FTBTeamsAPI.api().getManager();
            if (manager.arePlayersInSameTeam(actor.getUUID(), target.getUUID())) {
                return true;
            }
        }
        return actor.isAlliedTo(target);
    }

    public AlliedCondition() {
        super(NoConfiguration.CODEC);
    }

    @Override
    protected boolean check(NoConfiguration configuration, Entity actor, Entity target) {
        return isAllied(actor, target);
    }
}
