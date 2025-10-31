package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.ModifyPierceLevelPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(method = "shoot(DDDFF)V", at = @At("HEAD"))
    private void medievalorigins$modifyPierceLevel(double x, double y, double z, float speed, float divergence, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Entity owner = arrow.getOwner();

        if (owner != null) {
            for (ModifyPierceLevelPower power : PowerHolderComponent.getPowers(owner, ModifyPierceLevelPower.class)) {
                if (power.isActive()) {
                    float modifiedPierce = PowerHolderComponent.modify(owner, ModifyPierceLevelPower.class, 0.0f);
                    arrow.setPierceLevel((byte) Math.round(modifiedPierce));
                    power.executeActions();
                }
            }
        }
    }
}

