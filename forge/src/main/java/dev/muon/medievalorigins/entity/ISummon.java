package dev.muon.medievalorigins.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * For entities summoned by spells.
 */
public interface ISummon extends OwnableEntity {
        /*
        Implementation sourced from Ars Nouveau, in compliance with the LGPL-v3.0 license
    */

    int getTicksLeft();

    void setTicksLeft(int ticks);

    default @Nullable LivingEntity getLivingEntity() {
        return this instanceof LivingEntity ? (LivingEntity) this : null;
    }

    @Nullable
    default UUID getOwnerUUID(){
        return getOwnerID();
    }

    @Nullable
    default LivingEntity getOwner(){
        if(this instanceof LivingEntity && ((Entity) this).getCommandSenderWorld() instanceof ServerLevel serverLevel){
            return (LivingEntity) this.getOwner(serverLevel);
        }
        return null;
    }

    void setOwnerID(UUID uuid);

    default void writeOwner(CompoundTag tag) {
        if (getOwnerUUID() != null)
            tag.putUUID("owner", getOwnerUUID());
    }

    default @Nullable Entity readOwner(ServerLevel world, CompoundTag tag) {
        return tag.contains("owner") ? world.getEntity(tag.getUUID("owner")) : null;
    }


    @Nullable
    @Deprecated(forRemoval = true) // Use getOwnerUUID
    default UUID getOwnerID(){
        return null;
    }

    @Deprecated(forRemoval = true) // Use getOwner
    default @Nullable Entity getOwner(ServerLevel world) {
        return getOwnerUUID() != null ? world.getEntity(getOwnerUUID()) : null;
    }
}