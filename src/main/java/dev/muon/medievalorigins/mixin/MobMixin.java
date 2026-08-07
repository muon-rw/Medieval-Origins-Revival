package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.MobsIgnorePowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Unique
    private boolean shouldIgnoreTarget(Player player) {
        return PowerHolderComponent.hasPowerType(
                player,
                MobsIgnorePowerType.class,
                powerType -> powerType.shouldIgnore((Mob)(Object)this, player)
        );
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
    private LivingEntity preventTargeting(LivingEntity target) {
        if (target instanceof Player player && shouldIgnoreTarget(player)) {
            return null;
        }
        return target;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkExistingTarget(CallbackInfo ci) {
        Mob mob = (Mob)(Object)this;
        LivingEntity target = mob.getTarget();

        if (target instanceof Player player && shouldIgnoreTarget(player)) {
            mob.setTarget(null);
        }
    }
}