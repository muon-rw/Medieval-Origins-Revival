package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.ModifyProjectileAccuracyPowerType;
import dev.muon.medievalorigins.power.ModifyProjectileVelocityPowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {

    @ModifyArgs(method = "shoot(DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void modifyProjectileVelocity(Args args) {
        Projectile projectile = (Projectile) (Object) this;
        Entity owner = projectile.getOwner();

        if (owner != null) {
            if (projectile instanceof AbstractArrow) {
                PowerHolderComponent.getPowerTypes(owner, ModifyProjectileVelocityPowerType.class)
                    .forEach(powerType -> {
                        Vec3 originalVelocity = args.get(0);
                        double modifiedX = ModifierUtil.applyModifiers(owner, powerType.getModifiers(), originalVelocity.x);
                        double modifiedY = ModifierUtil.applyModifiers(owner, powerType.getModifiers(), originalVelocity.y);
                        double modifiedZ = ModifierUtil.applyModifiers(owner, powerType.getModifiers(), originalVelocity.z);
                        args.set(0, new Vec3(modifiedX, modifiedY, modifiedZ));
                        powerType.executeActions();
                    });
            }
        }
    }

    @ModifyArgs(method = "shoot(DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;getMovementToShoot(DDDFF)Lnet/minecraft/world/phys/Vec3;"))
    private void modifyProjectileAccuracy(Args args) {
        Projectile projectile = (Projectile) (Object) this;
        Entity owner = projectile.getOwner();

        if (owner != null) {
            if (projectile instanceof AbstractArrow) {
                PowerHolderComponent.getPowerTypes(owner, ModifyProjectileAccuracyPowerType.class)
                    .forEach(powerType -> {
                        float originalInaccuracy = args.get(4);
                        float modifiedInaccuracy = (float) ModifierUtil.applyModifiers(owner, powerType.getModifiers(), originalInaccuracy);
                        args.set(4, Math.max(0F, modifiedInaccuracy));
                        powerType.executeActions();
                    });
            }
        }
    }
} 