package dev.muon.medievalorigins.entity;

import dev.muon.medievalorigins.util.AllianceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import javax.annotation.Nullable;
import java.util.UUID;

public interface SummonedMob extends OwnableEntity {
    int getLifeTicks();

    void setLifeTicks(int lifeTicks);

    void setIsLimitedLife(boolean bool);

    boolean isLimitedLife();

    int getMaxLifeTicks();

    void setWeapon(ItemStack item);

    void reassessWeaponGoal();

    boolean isOrderedToSit();

    void setOrderedToSit(boolean sitting);

    Level getWorld();

    Mob getSelfAsMob();
    
    // Teleportation constants
    int TELEPORT_WHEN_DISTANCE_IS_SQ = 144; // 12 blocks squared
    int MIN_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 2;
    int MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 3;
    
    default boolean shouldTryTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        Mob self = this.getSelfAsMob();
        return owner != null && self.distanceToSqr(owner) >= TELEPORT_WHEN_DISTANCE_IS_SQ;
    }
    
    default void tryToTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            this.teleportToAroundBlockPos(owner.blockPosition());
        }
    }
    
    default void teleportToAroundBlockPos(BlockPos pos) {
        Mob self = this.getSelfAsMob();
        for (int i = 0; i < 10; i++) {
            int j = self.getRandom().nextIntBetweenInclusive(-MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING, 
                                                         MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING);
            int k = self.getRandom().nextIntBetweenInclusive(-MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING, 
                                                         MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING);
            if (Math.abs(j) >= MIN_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING || 
                Math.abs(k) >= MIN_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING) {
                int l = self.getRandom().nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }
    
    default boolean maybeTeleportTo(int x, int y, int z) {
        Mob self = this.getSelfAsMob();
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            self.moveTo(x + 0.5, y, z + 0.5, self.getYRot(), self.getXRot());
            self.getNavigation().stop();
            return true;
        }
    }
    
    default boolean canTeleportTo(BlockPos pos) {
        Mob self = this.getSelfAsMob();
        Level level = self.level();
        PathType pathType = WalkNodeEvaluator.getPathTypeStatic(self, pos);
        if (pathType != PathType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = level.getBlockState(pos.below());
            // Most summons can't fly, so don't teleport onto leaves
            if (blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(self.blockPosition());
                return level.noCollision(self, self.getBoundingBox().move(blockPos));
            }
        }
    }
    
    default boolean unableToMoveToOwner() {
        Mob self = this.getSelfAsMob();
        return this.isOrderedToSit() || 
               self.isPassenger() || 
               self.isLeashed() || 
               (this.getOwner() != null && this.getOwner().isSpectator());
    }

    @Override
    @Nullable
    UUID getOwnerUUID();

    void setOwnerID(UUID uuid);

    default void setOwner(LivingEntity owner) {
        setOwnerID(owner.getUUID());
    }

    default void setKillCredit(Entity target) {
        LivingEntity owner = getOwner();
        if (target instanceof LivingEntity livingTarget && owner instanceof Player player) {
            livingTarget.setLastHurtByPlayer(player);
        }
    }

    default boolean isAlliedOwner(UUID otherOwnerId) {
        UUID myOwnerId = getOwnerUUID();
        if (myOwnerId == null || otherOwnerId == null) {
            return false;
        }

        if (myOwnerId.equals(otherOwnerId)) {
            return true;
        }

        if (AllianceUtil.areEntitiesAlliedByFTBTeams(myOwnerId, otherOwnerId, this.getWorld())) {
            return true;
        }

        LivingEntity myOwner = getOwner();
        Entity potentialOtherOwner = null;
        if (getWorld() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            potentialOtherOwner = serverLevel.getEntity(otherOwnerId);
        } else if (getWorld().isClientSide()){
            potentialOtherOwner = getWorld().getPlayerByUUID(otherOwnerId);
        }

        if (myOwner != null && potentialOtherOwner instanceof LivingEntity otherOwnerLiving) {
            return myOwner.isAlliedTo(otherOwnerLiving);
        }

        return false;
    }

    @Override
    default LivingEntity getOwner() {
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID != null) {
            Entity ownerEntity = getWorld().getPlayerByUUID(ownerUUID);
            if (ownerEntity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }
        return OwnableEntity.super.getOwner();
    }

    static boolean checkAllyStatus(SummonedMob mob, Entity target) {
        Mob self = mob.getSelfAsMob();
        if (target == self) {
            return true;
        }

        LivingEntity owner = mob.getOwner();
        UUID ownerId = mob.getOwnerUUID();

        if (owner == null || ownerId == null) {
            return false;
        }

        if (target == owner) {
            return true;
        }

        UUID targetAffiliationId = AllianceUtil.getPlayerAffiliationIdRecursive(target);
        if (AllianceUtil.areEntitiesAlliedByFTBTeams(ownerId, targetAffiliationId, mob.getWorld())) {
            return true;
        }

        if (target instanceof OwnableEntity ownableTarget) {
            return mob.isAlliedOwner(ownableTarget.getOwnerUUID());
        }

        return owner.isAlliedTo(target);
    }
}