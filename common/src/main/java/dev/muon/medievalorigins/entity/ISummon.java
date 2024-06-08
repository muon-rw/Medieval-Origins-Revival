package dev.muon.medievalorigins.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public interface ISummon extends OwnableEntity {
    /*
    * Originally based off of Ars Nouveau, which is under the LGPL-v3.0 license
    */

    int getTicksLeft();

    default @Nullable LivingEntity getLivingEntity() {
        return this instanceof LivingEntity ? (LivingEntity) this : null;
    }
    void setLifeTicks(int lifeTicks);
    void setIsLimitedLife(boolean bool);
    void setWeapon(ItemStack item);
    void setOwner(LivingEntity owner);
    void setOwnerID(UUID uuid);
    void reassessWeaponGoal();

    @Nullable
    default UUID getOwnerUUID(){
        return null;
    }
    @Nullable
    default LivingEntity getOwner(){
        if(this instanceof LivingEntity && ((Entity) this).getCommandSenderWorld() instanceof ServerLevel serverLevel){
            return (LivingEntity) this.getOwner(serverLevel);
        }
        return null;
    }
    LivingEntity getOwnerFromID();
    @Deprecated(forRemoval = true) // Use getOwner
    default @Nullable Entity getOwner(ServerLevel world) {
        return getOwnerUUID() != null ? world.getEntity(getOwnerUUID()) : null;
    }
}