package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.power.MobsIgnorePower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TargetingConditions.class)
public class TargetingConditionsMixin {
    @ModifyReturnValue(method = "test(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"))
    private boolean preventTargeting(boolean original, LivingEntity attacker, LivingEntity target) {
        if (original && attacker != null && target instanceof Player player) {
            boolean shouldIgnore = PowerHolderComponent.getPowers(player, MobsIgnorePower.class).stream()
                    .filter(MobsIgnorePower::isActive)
                    .anyMatch(power -> power.shouldIgnore(attacker, player));
            if (shouldIgnore) {
                return false;
            }
        }
        return original;
    }
}
