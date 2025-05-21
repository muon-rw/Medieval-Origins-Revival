package dev.muon.medievalorigins.condition.bientity;

import dev.muon.medievalorigins.condition.ModBientityConditionTypes;
import dev.muon.medievalorigins.entity.SummonedMob;
import dev.muon.medievalorigins.util.AllianceUtil;
import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.BiEntityConditionContext;
import io.github.apace100.apoli.condition.type.BiEntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.OwnableEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AlliedConditionType extends BiEntityConditionType {
    public static final TypedDataObjectFactory<AlliedConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData(),
            data -> new AlliedConditionType(),
            (type, data) -> data.instance()
    );

    @Override
    public boolean test(BiEntityConditionContext context) {
        Entity actor = context.actor();
        Entity target = context.target();
        return isAllied(actor, target);
    }

    public static boolean isAllied(Entity actor, Entity target) {
        if (actor == target) {
            return true;
        }

        if (actor instanceof SummonedMob summonedActor) {
            return SummonedMob.checkAllyStatus(summonedActor, target);
        }
        if (target instanceof SummonedMob summonedTarget) {
            return SummonedMob.checkAllyStatus(summonedTarget, actor);
        }

        UUID actorAffiliationId = AllianceUtil.getPlayerAffiliationIdRecursive(actor);
        UUID targetAffiliationId = AllianceUtil.getPlayerAffiliationIdRecursive(target);

        if (AllianceUtil.areEntitiesAlliedByFTBTeams(actorAffiliationId, targetAffiliationId, actor.level())) {
            return true;
        }
        
        return actor.isAlliedTo(target);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ModBientityConditionTypes.ALLIED;
    }
}