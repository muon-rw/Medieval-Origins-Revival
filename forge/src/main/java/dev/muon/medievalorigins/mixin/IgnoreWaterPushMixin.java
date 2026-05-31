package dev.muon.medievalorigins.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.IgnoreWaterPower;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reimplements the current-push and swim-state halves of {@code origins:ignore_water} on
 * Forge/Connector. See {@link IgnoreWaterMixin} for the buoyancy half and the rationale for
 * not relying on Apoli's abandoned Connector integration.
 *
 * <p>Forge gates water-current pushing in {@code Entity#updateFluidHeightAndDoFluidPushing}
 * behind {@code isPushedByFluid(FluidType)}, whose Forge default delegates to the no-arg
 * {@link Entity#isPushedByFluid()}. Suppressing the no-arg therefore stops flowing water from
 * dragging a holder around. We additionally clear the swimming flag so a holder walks instead of
 * snapping into the swim pose.
 *
 * <p>The push hook is guarded to non-lava so lava current handling is left intact.
 */
@Mixin(Entity.class)
public abstract class IgnoreWaterPushMixin {

    @Inject(method = "isPushedByFluid", at = @At("HEAD"), cancellable = true)
    private void medievalorigins$ignoreWaterPush(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (!self.isInLava() && PowerHolderComponent.hasPower(self, IgnoreWaterPower.class)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateSwimming", at = @At("TAIL"))
    private void medievalorigins$ignoreWaterNoSwim(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.isSwimming() && PowerHolderComponent.hasPower(self, IgnoreWaterPower.class)) {
            self.setSwimming(false);
        }
    }
}
