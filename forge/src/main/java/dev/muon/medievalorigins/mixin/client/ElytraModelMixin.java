package dev.muon.medievalorigins.mixin.client;

import dev.muon.medievalorigins.power.PixieWingsPower;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraModel.class)
public abstract class ElytraModelMixin<T extends LivingEntity> {

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    private void modifyElytraRotations(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer player && PixieWingsPower.hasPower(player)) {
            player.elytraRotX += (0.8981317F - player.elytraRotX) * 0.1F;
            player.elytraRotY += (0.08726646F - player.elytraRotY) * 0.1F;
            player.elytraRotZ += (-0.5F - (float)Math.PI/4F - player.elytraRotZ) * 0.1F;
        }
    }
}