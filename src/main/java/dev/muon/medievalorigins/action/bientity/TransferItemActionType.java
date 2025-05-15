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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TransferItemActionType extends BiEntityActionType {

    private final EquipmentSlot sourceSlot;
    private final EquipmentSlot targetSlot;
    private final boolean prioritizeMainhandForTake;
    private final boolean allowEmptyHandMainhandTake;
    private static final double MAX_INTERACTION_DISTANCE = 6.0;

    public TransferItemActionType(SerializableData.Instance data) {
        this.sourceSlot = data.get("source_slot");
        this.targetSlot = data.get("target_slot");
        this.prioritizeMainhandForTake = data.get("prioritize_mainhand_for_take");
        this.allowEmptyHandMainhandTake = data.get("allow_empty_hand_mainhand_take");
    }

    public static final TypedDataObjectFactory<TransferItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("source_slot", SerializableDataTypes.EQUIPMENT_SLOT, EquipmentSlot.MAINHAND)
                    .add("target_slot", SerializableDataTypes.EQUIPMENT_SLOT, null)
                    .add("prioritize_mainhand_for_take", SerializableDataTypes.BOOLEAN, false)
                    .add("allow_empty_hand_mainhand_take", SerializableDataTypes.BOOLEAN, false),
            TransferItemActionType::new,
            (type, data) -> data.instance()
                    .set("source_slot", type.sourceSlot)
                    .set("target_slot", type.targetSlot)
                    .set("prioritize_mainhand_for_take", type.prioritizeMainhandForTake)
                    .set("allow_empty_hand_mainhand_take", type.allowEmptyHandMainhandTake)
    );

    @Override
    public void accept(BiEntityActionContext context) {
        Entity actor = context.actor();
        Entity target = context.target();

        if (actor.level().isClientSide()) {
            return;
        }

        Vec3 hitVec = null;
        if (!(actor instanceof LivingEntity livingActor && target instanceof LivingEntity livingTarget)) {
            return;
        }

        Vec3 eyePosition = livingActor.getEyePosition();
        Vec3 lookVector = livingActor.getLookAngle();
        AABB targetBoundingBox = livingTarget.getBoundingBox();
        Optional<Vec3> clipResult = targetBoundingBox.clip(eyePosition, eyePosition.add(lookVector.scale(MAX_INTERACTION_DISTANCE)));

        if (clipResult.isPresent()) {
            hitVec = clipResult.get();
        }

        ItemStack playerItemInSourceSlot = livingActor.getItemBySlot(this.sourceSlot).copy();

        if (playerItemInSourceSlot.isEmpty()) {
            handleTakingItem(livingActor, livingTarget, hitVec);
        } else {
            handleGivingOrSwappingItem(livingActor, livingTarget, playerItemInSourceSlot, hitVec);
        }
    }

    private void handleTakingItem(LivingEntity livingActor, LivingEntity livingTarget, @Nullable Vec3 hitVec) {
        EquipmentSlot slotOnSummonToInteract = null;

        if (this.prioritizeMainhandForTake) {
            if (!livingTarget.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                slotOnSummonToInteract = EquipmentSlot.MAINHAND;
            } else if (hitVec != null) {
                slotOnSummonToInteract = getClickedSlotOnSummon(livingTarget, hitVec, true);
            }
        } else {
            if (hitVec != null) {
                slotOnSummonToInteract = getClickedSlotOnSummon(livingTarget, hitVec, true);
            }
            if (slotOnSummonToInteract == null && !livingTarget.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                slotOnSummonToInteract = EquipmentSlot.MAINHAND;
            }
        }

        if (slotOnSummonToInteract != null) {
            if (slotOnSummonToInteract == EquipmentSlot.MAINHAND && !this.allowEmptyHandMainhandTake) {
                return;
            }

            ItemStack summonItemInClickedSlot = livingTarget.getItemBySlot(slotOnSummonToInteract).copy();
            if (!summonItemInClickedSlot.isEmpty()) {
                livingActor.setItemSlot(this.sourceSlot, summonItemInClickedSlot);
                livingTarget.setItemSlot(slotOnSummonToInteract, ItemStack.EMPTY);

                if (livingTarget instanceof SummonedMob summon && slotOnSummonToInteract == EquipmentSlot.MAINHAND) {
                    summon.reassessWeaponGoal();
                }
            }
        }
    }

    private void handleGivingOrSwappingItem(LivingEntity livingActor, LivingEntity livingTarget, ItemStack playerItemInSourceSlot, @Nullable Vec3 hitVec) {
        EquipmentSlot slotOnSummonToInteract;
        if (this.targetSlot != null) {
            slotOnSummonToInteract = this.targetSlot;
        } else {
            slotOnSummonToInteract = getTargetSlotForPlayerItem(playerItemInSourceSlot, EquipmentSlot.MAINHAND);
        }

        if (slotOnSummonToInteract == null) {
             return;
        }

        ItemStack summonItemInTargetSlot = livingTarget.getItemBySlot(slotOnSummonToInteract).copy();

        livingTarget.setItemSlot(slotOnSummonToInteract, playerItemInSourceSlot);
        livingActor.setItemSlot(this.sourceSlot, summonItemInTargetSlot);

        if (livingTarget instanceof Mob mob) {
            mob.setDropChance(slotOnSummonToInteract, 1.0f);
            mob.setPersistenceRequired();

            if (mob instanceof SummonedMob summon && slotOnSummonToInteract == EquipmentSlot.MAINHAND) {
                summon.reassessWeaponGoal();
            }
        }
    }

    private EquipmentSlot getTargetSlotForPlayerItem(ItemStack stack, EquipmentSlot defaultSlot) {
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

    @Nullable
    private EquipmentSlot getClickedSlotOnSummon(LivingEntity summon, Vec3 worldHitPos, boolean forTakingItem) {
        double localOriginY = summon.getY();
        double hitY = worldHitPos.y();
        double entityHeight = summon.getBbHeight();

        if (entityHeight <= 1e-5) {
            EquipmentSlot fallbackSlot = EquipmentSlot.MAINHAND;
            if (forTakingItem && summon.getItemBySlot(fallbackSlot).isEmpty()) {
                return null;
            }
            return fallbackSlot;
        }

        double clampedHitY = Math.max(localOriginY, Math.min(hitY, localOriginY + entityHeight));
        double normalizedHitY = (clampedHitY - localOriginY) / entityHeight;

        EquipmentSlot determinedArmorSlot = null;

        if (normalizedHitY >= 0.0 && normalizedHitY < 0.15) determinedArmorSlot = EquipmentSlot.FEET;
        else if (normalizedHitY >= 0.15 && normalizedHitY < 0.50) determinedArmorSlot = EquipmentSlot.LEGS;
        else if (normalizedHitY >= 0.50 && normalizedHitY < 0.85) determinedArmorSlot = EquipmentSlot.CHEST;
        else if (normalizedHitY >= 0.85 && normalizedHitY <= 1.0) determinedArmorSlot = EquipmentSlot.HEAD;
        else if (normalizedHitY > 1.0 && normalizedHitY <= 1.15 && summon.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem) determinedArmorSlot = EquipmentSlot.HEAD;

        if (determinedArmorSlot != null) {
            if (forTakingItem) {
                ItemStack itemInDeterminedSlot = summon.getItemBySlot(determinedArmorSlot);
                if (!itemInDeterminedSlot.isEmpty()) {
                    return determinedArmorSlot;
                } else {
                    if (determinedArmorSlot == EquipmentSlot.HEAD) {
                        if (!summon.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) return EquipmentSlot.CHEST;
                    } else if (determinedArmorSlot == EquipmentSlot.CHEST) {
                        if (!summon.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) return EquipmentSlot.HEAD;
                        else if (!summon.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) return EquipmentSlot.LEGS;
                    } else if (determinedArmorSlot == EquipmentSlot.LEGS) {
                        if (!summon.getItemBySlot(EquipmentSlot.FEET).isEmpty()) return EquipmentSlot.FEET;
                        else if (!summon.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) return EquipmentSlot.CHEST;
                    } else if (determinedArmorSlot == EquipmentSlot.FEET) {
                        if (!summon.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) return EquipmentSlot.LEGS;
                    }
                    return null;
                }
            } else {
                return determinedArmorSlot;
            }
        }

        EquipmentSlot fallbackSlot = EquipmentSlot.MAINHAND;
        if (forTakingItem) {
            return summon.getItemBySlot(fallbackSlot).isEmpty() ? null : fallbackSlot;
        } else {
            return fallbackSlot;
        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModBientityActionTypes.TRANSFER_ITEM;
    }
}