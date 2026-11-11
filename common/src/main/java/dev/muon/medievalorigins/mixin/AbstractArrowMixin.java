package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.ModifyPierceLevelPower;
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
    protected abstract void setPierceLevel(byte pierceLevel);

    @Shadow
    public abstract byte getPierceLevel();

    // At RETURN so the arrow's existing pierce level (e.g. from Piercing) is the modifiers' base value
    @Inject(method = "shoot(DDDFF)V", at = @At("RETURN"))
    private void modifyPierceLevel(double x, double y, double z, float velocity, float inaccuracy, CallbackInfo ci) {
        Entity owner = ((AbstractArrow) (Object) this).getOwner();
        if (owner != null && ModifyPierceLevelPower.isActive(owner)) {
            this.setPierceLevel((byte) Math.max(0, Math.round(ModifyPierceLevelPower.modify(owner, this.getPierceLevel()))));
            ModifyPierceLevelPower.runSelfActions(owner);
        }
    }
}
