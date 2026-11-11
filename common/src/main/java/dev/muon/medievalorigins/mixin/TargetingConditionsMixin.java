package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.MobsIgnorePower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public class TargetingConditionsMixin {

    @Inject(method = "test(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void preventTargetingIgnoringPlayer(LivingEntity source, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && MobsIgnorePower.isIgnoredBy(player, source)) {
            cir.setReturnValue(false);
        }
    }
}
