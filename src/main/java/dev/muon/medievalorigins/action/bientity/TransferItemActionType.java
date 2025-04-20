package dev.muon.medievalorigins.action.bientity;

import dev.muon.medievalorigins.action.ModBientityActionTypes;
import dev.muon.medievalorigins.entity.SummonedMob;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.BiEntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

public class TransferItemActionType extends BiEntityActionType {

    private final EquipmentSlot sourceSlot;
    private final EquipmentSlot targetSlot;
    private final boolean detectTargetSlot;

    public TransferItemActionType(SerializableData.Instance data) {
        this.sourceSlot = data.get("source_slot");
        this.targetSlot = data.get("target_slot");
        this.detectTargetSlot = data.get("detect_target_slot");
    }

    public static final TypedDataObjectFactory<TransferItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("source_slot", SerializableDataTypes.EQUIPMENT_SLOT, EquipmentSlot.MAINHAND)
                    .add("target_slot", SerializableDataTypes.EQUIPMENT_SLOT, null)
                    .add("detect_target_slot", SerializableDataTypes.BOOLEAN, false),
            TransferItemActionType::new,
            (type, data) -> data.instance()
                    .set("source_slot", type.sourceSlot)
                    .set("target_slot", type.targetSlot)
                    .set("detect_target_slot", type.detectTargetSlot)
    );

    @Override
    public void accept(BiEntityActionContext context) {
        Entity actor = context.actor();
        Entity target = context.target();

        if (actor instanceof LivingEntity livingActor && target instanceof LivingEntity livingTarget) {

            ItemStack itemToTransfer = livingActor.getItemBySlot(this.sourceSlot).copy();

            EquipmentSlot finalTargetSlot;
            boolean isOwnedSummon = target instanceof SummonedMob summon && livingActor.getUUID().equals(summon.getOwnerUUID());

            if (this.detectTargetSlot) {
                finalTargetSlot = getTargetSlot(itemToTransfer, EquipmentSlot.MAINHAND);
            }
            else if (this.targetSlot != null) {
                finalTargetSlot = this.targetSlot;
            } else if (isOwnedSummon) {
                finalTargetSlot = getTargetSlot(itemToTransfer, EquipmentSlot.MAINHAND);
            } else {
                finalTargetSlot = EquipmentSlot.MAINHAND;
            }


            if (finalTargetSlot == null) {
                return;
            }

            ItemStack itemInTargetSlot = livingTarget.getItemBySlot(finalTargetSlot).copy();

            livingTarget.setItemSlot(finalTargetSlot, itemToTransfer);
            livingActor.setItemSlot(this.sourceSlot, itemInTargetSlot);

            if (livingTarget instanceof Mob mob) {
                mob.setDropChance(finalTargetSlot, 1.0f);
                mob.setPersistenceRequired();

                if (isOwnedSummon && finalTargetSlot == EquipmentSlot.MAINHAND) {
                    ((SummonedMob) target).reassessWeaponGoal(); // Safe cast due to isOwnedSummon check
                }
            }
        }
    }

    private EquipmentSlot getTargetSlot(ItemStack stack, EquipmentSlot defaultSlot) {
        if (stack.isEmpty()) {
            return defaultSlot;
        }

        Item item = stack.getItem();

        if (item instanceof ShieldItem) return EquipmentSlot.OFFHAND;

        if (item instanceof ArmorItem armorItem) {
            return armorItem.getEquipmentSlot();
        }

        if (item instanceof ProjectileWeaponItem ||
            item instanceof SwordItem ||
            item instanceof DiggerItem ||
            item instanceof TridentItem) {
            return EquipmentSlot.MAINHAND;
        }

        return defaultSlot;
    }


    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModBientityActionTypes.TRANSFER_ITEM;
    }
}