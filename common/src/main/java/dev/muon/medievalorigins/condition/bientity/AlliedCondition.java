package dev.muon.medievalorigins.condition.bientity;

import com.mojang.serialization.MapCodec;
import dev.muon.medievalorigins.entity.SummonedMob;
import dev.muon.medievalorigins.util.AllianceUtil;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public final class AlliedCondition implements ConditionType<BiEntityCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, BiEntityCtx ctx) {
        return isAllied(ctx.actor(), ctx.target());
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
}