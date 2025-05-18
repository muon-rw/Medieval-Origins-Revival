package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.ModifyPierceLevelPowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Shadow
    private void setPierceLevel(byte pierceLevel) {
        throw new IllegalStateException();
    }

    @Inject(method = "shoot(DDDFF)V", at = @At("HEAD"))
    private void modifyPierceLevel(double x, double y, double z, float speed, float divergence, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Entity owner = arrow.getOwner();

        if (owner != null) {
            PowerHolderComponent.getPowerTypes(owner, ModifyPierceLevelPowerType.class).stream()
                .filter(ModifyPierceLevelPowerType::isActive)
                .forEach(powerType -> {
                    double modifiedPierce = ModifierUtil.applyModifiers(owner, powerType.getModifiers(), 0.0);
                    this.setPierceLevel((byte) Math.round(modifiedPierce));
                    powerType.executeActions();
                });
        }
    }
} 