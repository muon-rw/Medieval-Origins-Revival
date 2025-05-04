package dev.muon.medievalorigins.entity;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamManager;
import net.fabricmc.loader.api.FabricLoader;
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

        if (FabricLoader.getInstance().isModLoaded("ftbteams")) {
            boolean managerAvailable = this.getWorld().isClientSide() ?
                    FTBTeamsAPI.api().isClientManagerLoaded() :
                    FTBTeamsAPI.api().isManagerLoaded();
            if (!managerAvailable) {
                return true;
            }

            TeamManager manager = FTBTeamsAPI.api().getManager();
            if (manager.arePlayersInSameTeam(myOwnerId, otherOwnerId)) {
                return true;
            }
        }

        LivingEntity myOwner = getOwner();
        Entity potentialOtherOwner = null;
        if (getWorld() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
             potentialOtherOwner = serverLevel.getEntity(otherOwnerId);
        } else if (getWorld().isClientSide() && getWorld().getPlayerByUUID(otherOwnerId) != null){
            potentialOtherOwner = getWorld().getPlayerByUUID(otherOwnerId);
        }


        if (potentialOtherOwner instanceof LivingEntity otherOwner) {
            return myOwner != null && myOwner.isAlliedTo(otherOwner);
        }

        return false;
    }

    @Override
    default LivingEntity getOwner() {
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID != null) {
            Entity owner = getWorld().getPlayerByUUID(ownerUUID);
            if (owner instanceof LivingEntity livingEntity) {
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

        // Case 1: Owner exists
        if (owner != null) {
            if (target == owner) {
                return true;
            }

            UUID ownerId = mob.getOwnerUUID(); // Known not null because owner exists

            // FTB Teams Check (if applicable)
            if (FabricLoader.getInstance().isModLoaded("ftbteams")) {
                TeamManager manager = FTBTeamsAPI.api().getManager();
                if (target instanceof Player targetPlayer) {
                    if (manager.arePlayersInSameTeam(ownerId, targetPlayer.getUUID())) {
                        return true;
                    }
                } else if (target instanceof OwnableEntity ownableTarget) {
                    UUID targetOwnerId = ownableTarget.getOwnerUUID();
                    if (targetOwnerId != null && manager.arePlayersInSameTeam(ownerId, targetOwnerId)) {
                        return true;
                    }
                }
            }

            // Check if target is Ownable (covers other SummonedMobs) using vanilla/FTB teams of owners
            if (target instanceof OwnableEntity ownableTarget) {
                return mob.isAlliedOwner(ownableTarget.getOwnerUUID());
            }

            // Fallback: Use owner's vanilla isAlliedTo check for other entity types
            return owner.isAlliedTo(target);
        }
        // Case 2: No owner
        else {
            // If there's no owner, it can't be allied through ownership.
            // Reverting to false as a safe default.
            return false;
        }
    }
}