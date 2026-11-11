package dev.muon.medievalorigins.mixin.compat.icarus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.cammiescorner.icarus.client.IcarusClient;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.power.FaeWingsPower;
import dev.muon.medievalorigins.power.IcarusWingsPower;
import dev.muon.medievalorigins.power.PixieWingsPower;
import dev.muon.medievalorigins.util.ItemDataUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = IcarusClient.class, remap = false)
public abstract class IcarusClientMixin {

    @ModifyExpressionValue(method = "onPlayerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getArmorValue()I", remap = true))
    private static int excludeFeatherweightArmor(int original, AbstractClientPlayer player) {
        int armorValue = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof ArmorItem armor
                    && ItemDataUtil.getEnchantmentLevel(stack, ModEnchantments.FEATHERWEIGHT, player.level()) <= 0) {
                armorValue += armor.getDefense();
            }
        }
        return armorValue;
    }

    @ModifyReturnValue(method = "getWingsForRendering", at = @At("RETURN"))
    private static ItemStack renderPowerWings(ItemStack original, LivingEntity entity) {
        // Pixies and Fae render elytra-style wings, which Icarus wings would clip through
        if (PixieWingsPower.isActive(entity) || FaeWingsPower.isActive(entity)) {
            return ItemStack.EMPTY;
        }
        if (original.isEmpty()) {
            IcarusWingsPower.Config cfg = IcarusWingsPower.get(entity);
            if (cfg != null) {
                return cfg.wingsType();
            }
        }
        return original;
    }
}
