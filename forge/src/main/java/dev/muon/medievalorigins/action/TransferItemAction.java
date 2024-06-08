package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.entity.ISummon;
import dev.muon.medievalorigins.entity.SummonedSkeleton;
import dev.muon.medievalorigins.entity.SummonedWitherSkeleton;
import dev.muon.medievalorigins.entity.SummonedZombie;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


import java.util.UUID;
import java.util.function.BiConsumer;


public class TransferItemAction extends BiEntityAction<NoConfiguration> {

    public static void transferItem(Entity actor, Entity target) {
        if (!(actor instanceof Player player) || !(target instanceof LivingEntity livingTarget)) {
            return;
        }
        ItemStack actorItem = player.getMainHandItem().copy();
        ItemStack targetItem = livingTarget.getMainHandItem().copy();
        if (actorItem.isEmpty() && targetItem.isEmpty()) {
            return;
        }
        int actorSlotIndex = player.getInventory().selected;
        boolean shouldTransfer = false;
        if (target instanceof SummonedSkeleton summonedSkeleton) {
            shouldTransfer = player.getUUID().equals(summonedSkeleton.getOwnerUUID());
            if (shouldTransfer) summonedSkeleton.setWeapon(actorItem);
        } else if (target instanceof SummonedWitherSkeleton summonedWitherSkeleton) {
            shouldTransfer = player.getUUID().equals(summonedWitherSkeleton.getOwnerUUID());
            if (shouldTransfer) summonedWitherSkeleton.setWeapon(actorItem);
        } else if (target instanceof SummonedZombie summonedZombie) {
            shouldTransfer = player.getUUID().equals(summonedZombie.getOwnerUUID());
            if (shouldTransfer) summonedZombie.setWeapon(actorItem);
        } else {
            livingTarget.setItemInHand(InteractionHand.MAIN_HAND, actorItem);
            shouldTransfer = true;
        }
        if (shouldTransfer) {
            player.getInventory().setItem(actorSlotIndex, targetItem);
        }
    }

    private final BiConsumer<Entity, Entity> action;

    public TransferItemAction(BiConsumer<Entity, Entity> action) {
        super(NoConfiguration.CODEC);
        this.action = action;
    }

    @Override
    public void execute(NoConfiguration configuration, Entity actor, Entity target) {
        this.action.accept(actor, target);
    }

}