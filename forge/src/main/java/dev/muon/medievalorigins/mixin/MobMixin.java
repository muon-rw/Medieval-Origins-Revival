package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.power.MobsIgnorePower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {

    @Unique
    private boolean shouldIgnoreTarget(Player player) {
        Mob self = (Mob) (Object) this;
        return PowerHolderComponent.getPowers(player, MobsIgnorePower.class).stream()
                .filter(MobsIgnorePower::isActive)
                .anyMatch(power -> power.shouldIgnore(self, player));
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void preventTargeting(LivingEntity target, CallbackInfo ci) {
        if (target instanceof Player player && shouldIgnoreTarget(player)) {
            ci.cancel();
        }
    }
}
