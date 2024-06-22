package dev.muon.medievalorigins.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.muon.medievalorigins.power.PixieWingsPower;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElytraModel.class)
public abstract class ElytraModelMixin<T extends LivingEntity> {

    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z"))
    private boolean hasPixieWings(LivingEntity entity, Operation<Boolean> original) {
        if (entity.getDeltaMovement().y() > 0) {
            return PixieWingsPower.hasPower(entity);
        }
        return original.call(entity);
    }

    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/AbstractClientPlayer;elytraRotX:F", opcode = Opcodes.PUTFIELD))
    private void modifyElytraRotX(AbstractClientPlayer player, float value, Operation<Void> original) {
        if (PixieWingsPower.hasPower(player)) {
            value += (0.6981317F - value) * 0.1F;
        }
        original.call(player, value);
    }

    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/AbstractClientPlayer;elytraRotY:F", opcode = Opcodes.PUTFIELD))
    private void modifyElytraRotY(AbstractClientPlayer player, float value, Operation<Void> original) {
        if (PixieWingsPower.hasPower(player)) {
            value += (0.08726646F - value) * 0.1F;
        }
        original.call(player, value);
    }

    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/AbstractClientPlayer;elytraRotZ:F", opcode = Opcodes.PUTFIELD))
    private void modifyElytraRotZ(AbstractClientPlayer player, float value, Operation<Void> original) {
        if (PixieWingsPower.hasPower(player)) {
            value += (-0.7853982F - value) * 0.1F;
        }
        original.call(player, value);
    }
}