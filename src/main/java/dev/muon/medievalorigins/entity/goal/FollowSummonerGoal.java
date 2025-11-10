package dev.muon.medievalorigins.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import dev.muon.medievalorigins.entity.SummonedMob;
import net.minecraft.world.level.pathfinder.PathType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class FollowSummonerGoal extends Goal {
    private final SummonedMob summon;
    @Nullable
    private LivingEntity owner;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private final Map<PathType, Float> oldPathMalus = new HashMap<>();

    public FollowSummonerGoal(SummonedMob summon, double speedModifier, float startDistance, float stopDistance) {
        this.summon = summon;
        this.speedModifier = speedModifier;
        this.navigation = summon.getSelfAsMob().getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        
        // Verify we have a supported navigation type
        if (!(navigation instanceof GroundPathNavigation) && !(navigation instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowSummonerGoal");
        }
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = summon.getOwner();
        if (livingEntity == null) {
            return false;
        } else if (summon.unableToMoveToOwner()) {
            return false;
        } else if (summon.getSelfAsMob().distanceToSqr(livingEntity) < (startDistance * startDistance)) {
            return false;
        } else {
            this.owner = livingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (navigation.isDone()) {
            return false;
        } else if (summon.unableToMoveToOwner()) {
            return false;
        } else {
            return !(summon.getSelfAsMob().distanceToSqr(owner) <= (stopDistance * stopDistance));
        }
    }

    @Override
    public void start() {
        timeToRecalcPath = 0;
        Mob mob = summon.getSelfAsMob();
        
        // Store and modify path malus values for better following
        // Summons are fearless with fire/lava but avoid water
        for (PathType type : new PathType[]{
                PathType.WATER, 
                PathType.DAMAGE_FIRE, 
                PathType.DANGER_FIRE,
                PathType.DANGER_OTHER, 
                PathType.LAVA}) {
            oldPathMalus.put(type, mob.getPathfindingMalus(type));
            
            if (type == PathType.WATER) {
                // Increase water penalty - summons don't do well in water!
                // If already avoiding water, make it even more avoided
                float currentMalus = mob.getPathfindingMalus(type);
                mob.setPathfindingMalus(type, currentMalus < 0 ? currentMalus * 2.0F : -8.0F);
            } else {
                // Reduce fire/lava/danger penalties by half to make summons braver
                mob.setPathfindingMalus(type, mob.getPathfindingMalus(type) * 0.5F);
            }
        }
    }

    @Override
    public void stop() {
        owner = null;
        navigation.stop();
        
        // Restore original path malus values
        Mob mob = summon.getSelfAsMob();
        oldPathMalus.forEach((type, value) -> mob.setPathfindingMalus(type, value));
        oldPathMalus.clear();
    }

    @Override
    public void tick() {
        Mob mob = summon.getSelfAsMob();
        boolean shouldTeleport = summon.shouldTryTeleportToOwner();
        
        if (!shouldTeleport) {
            mob.getLookControl().setLookAt(owner, 10.0F, (float)mob.getMaxHeadXRot());
        }
        
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = adjustedTickDelay(10);
            if (shouldTeleport) {
                summon.tryToTeleportToOwner();
            } else {
                navigation.moveTo(owner, speedModifier);
            }
        }
    }
}