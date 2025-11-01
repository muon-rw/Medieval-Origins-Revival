package dev.muon.medievalorigins.condition.bientity;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.util.AllianceUtil;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class AlliedCondition {
    public static boolean condition(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        Entity actor = pair.getA();
        Entity target = pair.getB();

        return isAllied(actor, target);
    }

    public static boolean isAllied(Entity actor, Entity target) {
        if (actor == target) {
            return true;
        }

        UUID actorAffiliationId = AllianceUtil.getPlayerAffiliationIdRecursive(actor);
        UUID targetAffiliationId = AllianceUtil.getPlayerAffiliationIdRecursive(target);

        if (AllianceUtil.areEntitiesAlliedByFTBTeams(actorAffiliationId, targetAffiliationId, actor.level())) {
            return true;
        }

        return actor.isAlliedTo(target);
    }

    public static ConditionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ConditionFactory<>(MedievalOrigins.loc("allied"), new SerializableData(), AlliedCondition::condition);
    }
}
