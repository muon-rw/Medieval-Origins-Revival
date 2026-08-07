package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.muon.medievalorigins.power.ActionOnJumpPowerType;
import dev.muon.medievalorigins.power.ActionOnTargetDeathPowerType;
import dev.muon.medievalorigins.power.CustomDeathSoundPowerType;
import dev.muon.medievalorigins.power.MobsIgnorePowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private boolean shouldIgnoreTarget(Player player) {
        return PowerHolderComponent.hasPowerType(
                player,
                MobsIgnorePowerType.class,
                powerType -> powerType.shouldIgnore((LivingEntity) (Object) this, player)
        );
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void preventAttackValidation(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && shouldIgnoreTarget(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void preventAttackValidationWithConditions(LivingEntity target, TargetingConditions condition, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && shouldIgnoreTarget(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void invokeTargetDeathAction(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            PowerHolderComponent.withPowerTypes(attacker, ActionOnTargetDeathPowerType.class,
                    power -> power.doesApply((LivingEntity) (Object) this, source, amount),
                    power -> power.executeActions((LivingEntity) (Object) this, source, amount));
        }
    }

    @WrapOperation(method = "handleEntityEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V",
            ordinal = 0))
    private void onDeathSound(LivingEntity instance, SoundEvent soundEvent, float volume, float pitch, Operation<Void> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        var powers = PowerHolderComponent.getPowerTypes(self, CustomDeathSoundPowerType.class);

        if (!powers.isEmpty()) {
            boolean anyMuted = powers.stream().anyMatch(power -> power.isMuted());
            if (!anyMuted) {
                powers.forEach(power -> power.playDeathSound(self));
            }
        } else {
            original.call(instance, soundEvent, volume, pitch);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void onJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        PowerHolderComponent.getPowerTypes(self, ActionOnJumpPowerType.class)
                .forEach(power -> power.executeAction(self));
    }
}