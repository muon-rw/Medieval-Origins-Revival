package dev.muon.medievalorigins.entity;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
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
            boolean inSameTeam = false;
            if (this.getWorld().isClientSide()) {
                if (!FTBTeamsAPI.api().isClientManagerLoaded()) {
                    return true; // Permissive fallback: if client manager not loaded, assume allied
                }
                var clientManager = FTBTeamsAPI.api().getClientManager();
                Optional<KnownClientPlayer> owner1Opt = clientManager.getKnownPlayer(myOwnerId);
                Optional<KnownClientPlayer> owner2Opt = clientManager.getKnownPlayer(otherOwnerId);

                if (owner1Opt.isPresent() && owner2Opt.isPresent()) {
                    inSameTeam = owner1Opt.get().teamId().equals(owner2Opt.get().teamId());
                }
            } else {
                if (!FTBTeamsAPI.api().isManagerLoaded()) {
                    return true; // Permissive fallback: if server manager not loaded, assume allied
                }
                var serverManager = FTBTeamsAPI.api().getManager();
                inSameTeam = serverManager.arePlayersInSameTeam(myOwnerId, otherOwnerId);
            }

            if (inSameTeam) {
                return true;
            }
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

        if (FabricLoader.getInstance().isModLoaded("ftbteams")) {
            UUID targetAffiliationId = null;
            if (target instanceof Player targetPlayer) {
                targetAffiliationId = targetPlayer.getUUID();
            } else if (target instanceof OwnableEntity ownableTarget) {
                targetAffiliationId = ownableTarget.getOwnerUUID();
            }

            if (targetAffiliationId != null) {
                boolean managerAvailable = mob.getWorld().isClientSide() ?
                        FTBTeamsAPI.api().isClientManagerLoaded() :
                        FTBTeamsAPI.api().isManagerLoaded();

                if (!managerAvailable) {
                    return true; // Permissive fallback: if manager not loaded, assume allied (consistent with isAlliedOwner)
                }

                boolean ftbAllied = isFtbAllied(mob, ownerId, targetAffiliationId);

                if (ftbAllied) {
                    return true;
                }
            }
        }

        if (target instanceof OwnableEntity ownableTarget) {
            return mob.isAlliedOwner(ownableTarget.getOwnerUUID());
        }

        return owner.isAlliedTo(target);
    }

    private static boolean isFtbAllied(SummonedMob mob, UUID ownerId, UUID targetAffiliationId) {
        boolean ftbAllied = false;
        if (mob.getWorld().isClientSide()) {
            var clientManager = FTBTeamsAPI.api().getClientManager();
            Optional<KnownClientPlayer> mobOwnerOpt = clientManager.getKnownPlayer(ownerId);
            Optional<KnownClientPlayer> targetAffiliationOpt = clientManager.getKnownPlayer(targetAffiliationId);
            if (mobOwnerOpt.isPresent() && targetAffiliationOpt.isPresent() && mobOwnerOpt.get().teamId().equals(targetAffiliationOpt.get().teamId())) {
                ftbAllied = true;
            }
        } else {
            var serverManager = FTBTeamsAPI.api().getManager();
            if (serverManager.arePlayersInSameTeam(ownerId, targetAffiliationId)) {
                ftbAllied = true;
            }
        }
        return ftbAllied;
    }
}