package dev.muon.medievalorigins.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import dev.muon.medievalorigins.entity.SummonedMob;

public class SummonedMobGoal extends TargetGoal {
    private final SummonedMob summon;
    private LivingEntity ownerLastTarget;
    private int retargetCountdown;

    public SummonedMobGoal(Mob mob) {
        super(mob, false);
        this.summon = (SummonedMob) mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = summon.getOwner();
        if (owner == null) return false;

        LivingEntity ownerTarget = owner.getLastHurtMob();
        if (ownerTarget != null && ownerTarget != ownerLastTarget) {
            return isValidTarget(ownerTarget, owner);
        }

        // Check if owner was hurt by something
        ownerTarget = owner.getLastHurtByMob();
        return ownerTarget != null && ownerTarget != ownerLastTarget && isValidTarget(ownerTarget, owner);
    }

    @Override
    public void start() {
        LivingEntity owner = summon.getOwner();
        if (owner == null) return;

        LivingEntity target = owner.getLastHurtMob();
        if (target == null) {
            target = owner.getLastHurtByMob();
        }

        if (isValidTarget(target, owner)) {
            mob.setTarget(target);
            ownerLastTarget = target;
        }
        super.start();
    }

    private boolean isValidTarget(LivingEntity target, LivingEntity owner) {
        if (target == null || target == owner || target == mob) {
            return false;
        }

        // Don't attack other summons from the same owner
        if (target instanceof SummonedMob summonedTarget) {
            return !owner.getUUID().equals(summonedTarget.getOwnerUUID());
        }

        return TargetingConditions.DEFAULT.test(mob, target);
    }
}