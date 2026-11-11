package dev.muon.medievalorigins.action.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.entity.SummonedMob;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class TransferItemAction implements ActionType<BiEntityCtx, TransferItemAction.Cfg> {

    public record Cfg(
            EquipmentSlot sourceSlot,
            Optional<EquipmentSlot> targetSlot,
            boolean prioritizeMainhandForTake,
            boolean allowEmptyHandMainhandTake
    ) {}

    private static final Codec<EquipmentSlot> SLOT_CODEC = StringRepresentable.fromEnum(EquipmentSlot::values);

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            SLOT_CODEC.optionalFieldOf("source_slot", EquipmentSlot.MAINHAND).forGetter(Cfg::sourceSlot),
            SLOT_CODEC.optionalFieldOf("target_slot").forGetter(Cfg::targetSlot),
            Codec.BOOL.optionalFieldOf("prioritize_mainhand_for_take", false).forGetter(Cfg::prioritizeMainhandForTake),
            Codec.BOOL.optionalFieldOf("allow_empty_hand_mainhand_take", false).forGetter(Cfg::allowEmptyHandMainhandTake)
    ).apply(i, Cfg::new));

    private static final double MAX_INTERACTION_DISTANCE = 6.0;

    @Override
    public MapCodec<Cfg> codec() {
        return CODEC;
    }

    @Override
    public void run(Cfg cfg, BiEntityCtx ctx) {
        Entity actor = ctx.actor();
        Entity target = ctx.target();

        if (actor == null || target == null || actor.level().isClientSide()) {
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

        ItemStack playerItemInSourceSlot = livingActor.getItemBySlot(cfg.sourceSlot()).copy();

        if (playerItemInSourceSlot.isEmpty()) {
            handleTakingItem(cfg, livingActor, livingTarget, hitVec);
        } else {
            handleGivingOrSwappingItem(cfg, livingActor, livingTarget, playerItemInSourceSlot);
        }
    }

    private static void handleTakingItem(Cfg cfg, LivingEntity livingActor, LivingEntity livingTarget, @Nullable Vec3 hitVec) {
        EquipmentSlot slotOnSummonToInteract = null;

        if (cfg.prioritizeMainhandForTake()) {
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
            if (slotOnSummonToInteract == EquipmentSlot.MAINHAND && !cfg.allowEmptyHandMainhandTake()) {
                return;
            }

            ItemStack summonItemInClickedSlot = livingTarget.getItemBySlot(slotOnSummonToInteract).copy();
            if (!summonItemInClickedSlot.isEmpty()) {
                livingActor.setItemSlot(cfg.sourceSlot(), summonItemInClickedSlot);
                livingTarget.setItemSlot(slotOnSummonToInteract, ItemStack.EMPTY);

                if (livingTarget instanceof SummonedMob summon && slotOnSummonToInteract == EquipmentSlot.MAINHAND) {
                    summon.reassessWeaponGoal();
                }
            }
        }
    }

    private static void handleGivingOrSwappingItem(Cfg cfg, LivingEntity livingActor, LivingEntity livingTarget, ItemStack playerItemInSourceSlot) {
        EquipmentSlot slotOnSummonToInteract;
        slotOnSummonToInteract = cfg.targetSlot()
                .orElseGet(() -> getTargetSlotForPlayerItem(playerItemInSourceSlot, EquipmentSlot.MAINHAND));

        if (slotOnSummonToInteract == null) {
             return;
        }

        ItemStack summonItemInTargetSlot = livingTarget.getItemBySlot(slotOnSummonToInteract).copy();

        livingTarget.setItemSlot(slotOnSummonToInteract, playerItemInSourceSlot);
        livingActor.setItemSlot(cfg.sourceSlot(), summonItemInTargetSlot);

        if (livingTarget instanceof Mob mob) {
            mob.setDropChance(slotOnSummonToInteract, 1.0f);
            mob.setPersistenceRequired();

            if (mob instanceof SummonedMob summon && slotOnSummonToInteract == EquipmentSlot.MAINHAND) {
                summon.reassessWeaponGoal();
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

    @Nullable
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
