package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.MobsIgnorePower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Shadow
    public abstract LivingEntity getTarget();

    @Shadow
    public abstract void setTarget(LivingEntity target);

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
    private LivingEntity dropIgnoringPlayerTarget(LivingEntity target) {
        if (target instanceof Player player && MobsIgnorePower.isIgnoredBy(player, (Mob) (Object) this)) {
            return null;
        }
        return target;
    }

    // Targets acquired before the power applied would otherwise persist
    @Inject(method = "tick", at = @At("HEAD"))
    private void clearIgnoringPlayerTarget(CallbackInfo ci) {
        if (this.getTarget() instanceof Player player && MobsIgnorePower.isIgnoredBy(player, (Mob) (Object) this)) {
            this.setTarget(null);
        }
    }
}
