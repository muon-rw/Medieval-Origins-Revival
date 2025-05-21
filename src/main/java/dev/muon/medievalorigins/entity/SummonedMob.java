package dev.muon.medievalorigins.entity;

import dev.muon.medievalorigins.util.AllianceUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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