package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.ModifyProjectileAccuracyPower;
import dev.muon.medievalorigins.power.ModifyProjectileVelocityPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {

    @ModifyVariable(
            method = "shoot(DDDFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"),
            ordinal = 0
    )
    private Vec3 medievalorigins$modifyProjectileVelocity(Vec3 vec3) {
        Projectile projectile = (Projectile) (Object) this;
        Entity owner = projectile.getOwner();

        if (owner != null && projectile instanceof AbstractArrow) {
            double modifiedX = PowerHolderComponent.modify(owner, ModifyProjectileVelocityPower.class, (float) vec3.x);
            double modifiedY = PowerHolderComponent.modify(owner, ModifyProjectileVelocityPower.class, (float) vec3.y);
            double modifiedZ = PowerHolderComponent.modify(owner, ModifyProjectileVelocityPower.class, (float) vec3.z);

            for (ModifyProjectileVelocityPower power : PowerHolderComponent.getPowers(owner, ModifyProjectileVelocityPower.class)) {
                if (power.isActive()) {
                    power.executeActions();
                }
            }

            return new Vec3(modifiedX, modifiedY, modifiedZ);
        }

        return vec3;
    }

    @ModifyVariable(
            method = "shoot(DDDFF)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private float medievalorigins$modifyProjectileAccuracy(float inaccuracy) {
        Projectile projectile = (Projectile) (Object) this;
        Entity owner = projectile.getOwner();

        if (owner != null && projectile instanceof AbstractArrow) {
            float modifiedInaccuracy = PowerHolderComponent.modify(owner, ModifyProjectileAccuracyPower.class, inaccuracy);

            for (ModifyProjectileAccuracyPower power : PowerHolderComponent.getPowers(owner, ModifyProjectileAccuracyPower.class)) {
                if (power.isActive()) {
                    power.executeActions();
                }
            }

            return Math.max(0F, modifiedInaccuracy);
        }

        return inaccuracy;
    }
}
