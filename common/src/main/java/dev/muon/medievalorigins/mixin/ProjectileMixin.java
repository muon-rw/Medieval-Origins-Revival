package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.ModifyProjectileAccuracyPower;
import dev.muon.medievalorigins.power.ModifyProjectileVelocityPower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {

    @Shadow
    public abstract Entity getOwner();

    @ModifyVariable(method = "shoot(DDDFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private float modifyArrowInaccuracy(float inaccuracy) {
        Entity owner = this.getOwner();
        if (!((Object) this instanceof AbstractArrow) || owner == null || !ModifyProjectileAccuracyPower.isActive(owner)) {
            return inaccuracy;
        }
        return Math.max(0F, (float) ModifyProjectileAccuracyPower.modify(owner, inaccuracy));
    }

    @ModifyArg(method = "shoot(DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 modifyArrowVelocity(Vec3 velocity) {
        Entity owner = this.getOwner();
        if (!((Object) this instanceof AbstractArrow) || owner == null || !ModifyProjectileVelocityPower.isActive(owner)) {
            return velocity;
        }
        return new Vec3(
                ModifyProjectileVelocityPower.modify(owner, velocity.x),
                ModifyProjectileVelocityPower.modify(owner, velocity.y),
                ModifyProjectileVelocityPower.modify(owner, velocity.z));
    }

    @Inject(method = "shoot(DDDFF)V", at = @At("RETURN"))
    private void runArrowShotActions(double x, double y, double z, float velocity, float inaccuracy, CallbackInfo ci) {
        Entity owner = this.getOwner();
        if ((Object) this instanceof AbstractArrow && owner != null) {
            ModifyProjectileVelocityPower.runSelfActions(owner);
            ModifyProjectileAccuracyPower.runSelfActions(owner);
        }
    }
}
