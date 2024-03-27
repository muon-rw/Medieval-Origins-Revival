package dev.muon.medievalorigins.mixin;

import dev.cammiescorner.icarus.Icarus;
import dev.cammiescorner.icarus.common.items.WingItem;
import dev.cammiescorner.icarus.core.integration.IcarusConfig;
import dev.cammiescorner.icarus.core.util.IcarusHelper;
import dev.cammiescorner.icarus.core.util.SlowFallEntity;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.muon.medievalorigins.power.WingsPower;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static dev.muon.medievalorigins.MedievalOrigins.LOGGER;

@Mixin(value = WingItem.class, priority = 5000)
public class WingItemMixin {
    private static final TagKey<Item> MELTS;
    public boolean isUsable(ItemStack stack) {
        return IcarusConfig.wingsDurability <= 0 || stack.getDamageValue() < stack.getMaxDamage() - 1;
    }
    /**
     * @author muon-rw
     * @reason Replacing the Icarus wing tick logic to accommodate a workaround for Origins
     */
    @Overwrite
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!WingsPower.hasWingsPower(player)) {
                LOGGER.info("Player using wings, but failing wings power check");
                if (player.getFoodData().getFoodLevel() <= 6 || !this.isUsable(stack)) {
                    IcarusHelper.stopFlying(player);
                    return;
                }
            }
            if (WingsPower.hasWingsPower(player)) {
                LOGGER.info("Player passing wings power check");
            }
            if (player.isFallFlying()) {
                if (player.zza > 0.0F) {
                    IcarusHelper.applySpeed(player, stack);
                }

                if (IcarusConfig.canSlowFall && player.isShiftKeyDown() || player.isUnderWater()) {
                    IcarusHelper.stopFlying(player);
                }

                if (player.position().y > (double)(player.level().getHeight() + 64) && player.tickCount % 2 == 0 && stack.is(MELTS)) {
                    stack.hurtAndBreak(1, player, (p) -> {
                        p.broadcastBreakEvent(EquipmentSlot.CHEST);
                    });
                }
            } else {
                if (player.onGround() || player.isInWater()) {
                    ((SlowFallEntity)player).setSlowFalling(false);
                }

                if (((SlowFallEntity)player).isSlowFalling()) {
                    player.fallDistance = 0.0F;
                    player.setDeltaMovement(player.getDeltaMovement().x, -0.4, player.getDeltaMovement().z);
                }
            }
        }
    }
    static {
        MELTS = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("icarus", "melts"));
    }

}