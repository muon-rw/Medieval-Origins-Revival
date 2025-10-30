package dev.muon.medievalorigins.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.muon.medievalorigins.power.EdibleItemPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @WrapOperation(
            method = "getArmPose",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;")
    )
    private static UseAnim medievalorigins$customUseAnimationInArmPose(ItemStack stack, Operation<UseAnim> original, @Local(argsOnly = true) AbstractClientPlayer player) {
        UseAnim originalAnim = original.call(stack);
        
        // Check for edible item power
        for (EdibleItemPower power : PowerHolderComponent.getPowers(player, EdibleItemPower.class)) {
            if (power.doesApply(player.level(), stack)) {
                UseAnim customAnim = power.getUseAction();
                if (customAnim != null && customAnim != UseAnim.NONE) {
                    return customAnim;
                }
            }
        }
        
        return originalAnim;
    }
}

