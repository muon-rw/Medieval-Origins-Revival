package dev.muon.medievalorigins.action.bientity;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class TransferItemAction {

    private static final double MAX_INTERACTION_DISTANCE = 6.0;

    public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("transfer_item"),
                new SerializableData()
                        .add("source_slot", SerializableDataTypes.EQUIPMENT_SLOT, EquipmentSlot.MAINHAND)
                        .add("target_slot", SerializableDataTypes.EQUIPMENT_SLOT, null)
                        .add("prioritize_mainhand_for_take", SerializableDataTypes.BOOLEAN, false)
                        .add("allow_empty_hand_mainhand_take", SerializableDataTypes.BOOLEAN, false),
                TransferItemAction::action
        );
    }
    
    public static void action(SerializableData.Instance data, Tuple<Entity, Entity> entities) {
        Entity actor = entities.getA();
        Entity target = entities.getB();

        if (actor.level().isClientSide()) {
            return;
        }

        if (!(actor instanceof LivingEntity livingActor && target instanceof LivingEntity livingTarget)) {
            return;
        }

        // Extract configuration from data
        EquipmentSlot sourceSlot = data.get("source_slot");
        EquipmentSlot targetSlot = data.get("target_slot");
        boolean prioritizeMainhandForTake = data.get("prioritize_mainhand_for_take");
        boolean allowEmptyHandMainhandTake = data.get("allow_empty_hand_mainhand_take");

        Vec3 eyePosition = livingActor.getEyePosition();
        Vec3 lookVector = livingActor.getLookAngle();
        AABB targetBoundingBox = livingTarget.getBoundingBox();
        Optional<Vec3> clipResult = targetBoundingBox.clip(eyePosition, eyePosition.add(lookVector.scale(MAX_INTERACTION_DISTANCE)));

        Vec3 hitVec = clipResult.orElse(null);

        ItemStack playerItemInSourceSlot = livingActor.getItemBySlot(sourceSlot).copy();

        if (playerItemInSourceSlot.isEmpty()) {
            handleTakingItem(livingActor, livingTarget, hitVec, sourceSlot, prioritizeMainhandForTake, allowEmptyHandMainhandTake);
        } else {
            handleGivingOrSwappingItem(livingActor, livingTarget, playerItemInSourceSlot, hitVec, sourceSlot, targetSlot);
        }
    }

    private static void handleTakingItem(LivingEntity livingActor, LivingEntity livingTarget,  Vec3 hitVec,
                                          EquipmentSlot sourceSlot, boolean prioritizeMainhandForTake, boolean allowEmptyHandMainhandTake) {
        EquipmentSlot slotOnSummonToInteract = null;

        if (prioritizeMainhandForTake) {
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
            if (slotOnSummonToInteract == EquipmentSlot.MAINHAND && !allowEmptyHandMainhandTake) {
                return;
            }

            ItemStack summonItemInClickedSlot = livingTarget.getItemBySlot(slotOnSummonToInteract).copy();
            if (!summonItemInClickedSlot.isEmpty()) {
                livingActor.setItemSlot(sourceSlot, summonItemInClickedSlot);
                livingTarget.setItemSlot(slotOnSummonToInteract, ItemStack.EMPTY);

                if (livingTarget instanceof AbstractSkeleton skeleton && slotOnSummonToInteract == EquipmentSlot.MAINHAND) {
                    skeleton.reassessWeaponGoal();
                }
            }
        }
    }


    private static void handleGivingOrSwappingItem(LivingEntity livingActor, LivingEntity livingTarget, ItemStack playerItemInSourceSlot, 
                                                      Vec3 hitVec, EquipmentSlot sourceSlot, EquipmentSlot targetSlot) {
        EquipmentSlot slotOnSummonToInteract;
        if (targetSlot != null) {
            slotOnSummonToInteract = targetSlot;
        } else {
            slotOnSummonToInteract = getTargetSlotForPlayerItem(playerItemInSourceSlot, EquipmentSlot.MAINHAND);
        }

        if (slotOnSummonToInteract == null) {
            return;
        }

        ItemStack summonItemInTargetSlot = livingTarget.getItemBySlot(slotOnSummonToInteract).copy();

        livingTarget.setItemSlot(slotOnSummonToInteract, playerItemInSourceSlot);
        livingActor.setItemSlot(sourceSlot, summonItemInTargetSlot);

        if (livingTarget instanceof Mob mob) {
            mob.setDropChance(slotOnSummonToInteract, 1.0f);
            mob.setPersistenceRequired();

            if (mob instanceof AbstractSkeleton skeleton && slotOnSummonToInteract == EquipmentSlot.MAINHAND) {
                skeleton.reassessWeaponGoal();
            }
        }
    }

    private static EquipmentSlot getTargetSlotForPlayerItem(ItemStack stack, EquipmentSlot defaultSlot) {
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

    
    private static EquipmentSlot getClickedSlotOnSummon(LivingEntity summon, Vec3 worldHitPos, boolean forTakingItem) {
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
}
