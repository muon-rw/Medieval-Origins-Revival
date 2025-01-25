package dev.muon.medievalorigins.condition;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamManager;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class AlliedCondition {
    public static boolean condition(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        Entity actor = pair.getA();
        Entity target = pair.getB();

        if (FabricLoader.getInstance().isModLoaded("ftbteams")) {
            TeamManager manager = FTBTeamsAPI.api().getManager();
            if (manager.arePlayersInSameTeam(actor.getUUID(), target.getUUID())) {
                return true;
            }
        }
        return actor.isAlliedTo(target);
    }

    public static ConditionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ConditionFactory<>(MedievalOrigins.loc("allied"), new SerializableData(), AlliedCondition::condition);
    }
}