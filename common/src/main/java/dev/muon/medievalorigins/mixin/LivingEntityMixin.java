package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.muon.medievalorigins.power.ActionOnJumpPower;
import dev.muon.medievalorigins.power.ActionOnTargetDeathPower;
import dev.muon.medievalorigins.power.CustomDeathSoundPower;
import dev.muon.medievalorigins.power.MobsIgnorePower;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void preventAttackingIgnoringPlayer(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && MobsIgnorePower.isIgnoredBy(player, (LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;)Z",
            at = @At("HEAD"), cancellable = true)
    private void preventAttackingIgnoringPlayerWithConditions(LivingEntity target, TargetingConditions conditions, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && MobsIgnorePower.isIgnoredBy(player, (LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void runTargetDeathActions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof LivingEntity killer) {
            ActionOnTargetDeathPower.onTargetDeath(killer, (LivingEntity) (Object) this, source, amount);
        }
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;makeSound(Lnet/minecraft/sounds/SoundEvent;)V"))
    private void replaceDeathSound(LivingEntity instance, SoundEvent sound, Operation<Void> original) {
        if (!CustomDeathSoundPower.replaceDeathSound(instance)) {
            original.call(instance, sound);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void runJumpActions(CallbackInfo ci) {
        ActionOnJumpPower.onJump((LivingEntity) (Object) this);
    }
}
