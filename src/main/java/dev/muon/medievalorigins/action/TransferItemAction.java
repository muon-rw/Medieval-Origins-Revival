package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.SummonedSkeleton;
import dev.muon.medievalorigins.entity.SummonedWitherSkeleton;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;




public class TransferItemAction {
    public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ActionFactory<>(MedievalOrigins.loc("transfer_item"), new SerializableData(), TransferItemAction::executeAction);
    }
    public static void executeAction(SerializableData.Instance data, Tuple<Entity, Entity> entities) {
        Entity actor = entities.getA();
        Entity target = entities.getB();

        if (actor instanceof LivingEntity livingActor && target instanceof LivingEntity livingTarget) {
            ItemStack actorItem = livingActor.getMainHandItem().copy();
            ItemStack targetItem = livingTarget.getMainHandItem().copy();

            if (!actorItem.isEmpty() || !targetItem.isEmpty()) {
                if (target instanceof SummonedSkeleton summon) {
                    summon.setWeapon(actorItem.isEmpty() ? ItemStack.EMPTY : actorItem);
                } else if (target instanceof SummonedWitherSkeleton summon) {
                    summon.setWeapon(actorItem.isEmpty() ? ItemStack.EMPTY : actorItem);
                } else {
                    livingTarget.setItemInHand(InteractionHand.MAIN_HAND, actorItem.isEmpty() ? ItemStack.EMPTY : actorItem);
                }
                livingActor.setItemInHand(InteractionHand.MAIN_HAND, targetItem.isEmpty() ? ItemStack.EMPTY : targetItem);
            }
        }
    }
}
