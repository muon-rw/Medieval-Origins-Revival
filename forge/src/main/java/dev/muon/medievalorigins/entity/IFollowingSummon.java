package dev.muon.medievalorigins.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public interface IFollowingSummon {
        /*
        Implementation sourced from Ars Nouveau, in compliance with the LGPL-v3.0 license
    */

    EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.OPTIONAL_UUID);

    boolean wantsToAttack(LivingEntity pTarget, LivingEntity pOwner);

    Level getWorld();

    PathNavigation getPathNav();

    LivingEntity getSummoner();

    Mob getSelfEntity();

    class CopyOwnerTargetGoal<I extends PathfinderMob & IFollowingSummon> extends TargetGoal {

        public CopyOwnerTargetGoal(I creature) {
            super(creature, false);
        }

        @Override
        public boolean canUse() {
            if (!(this.mob instanceof IFollowingSummon summon)) return false;
            LivingEntity ownerLastHurt = summon.getSummoner() != null ? summon.getSummoner().getLastHurtMob() : null;
            // Check if the last entity hurt by the owner is a valid target
            return ownerLastHurt != null && this.isValidTarget(ownerLastHurt, summon.getSummoner());
        }

        @Override
        public void start() {
            if (mob instanceof IFollowingSummon summon && summon.getSummoner() != null) {
                LivingEntity target = summon.getSummoner().getLastHurtMob();
                if (this.isValidTarget(target, summon.getSummoner())) {
                    mob.setTarget(target);
                }
            }
            super.start();
        }

        private boolean isValidTarget(LivingEntity target, LivingEntity owner) {
            // Do not target the owner, itself, or other summons with the same owner
            if (target == owner || target == mob) {
                return false;
            }
            if (target instanceof ISummon) {
                UUID targetOwnerUUID = ((ISummon) target).getOwnerUUID();
                if (targetOwnerUUID != null && targetOwnerUUID.equals(((ISummon) mob).getOwnerUUID())) {
                    return false;
                }
            }
            // Additional checks can be added here
            return true;
        }
    }
}
