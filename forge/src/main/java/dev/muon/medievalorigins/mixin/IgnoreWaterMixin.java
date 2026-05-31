package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.IgnoreWaterPower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Reimplements the buoyancy half of {@code origins:ignore_water} on Forge/Connector.
 *
 * <p>Apoli's own {@code ignore_water} handling forces {@code Entity#isInWater()} to return
 * {@code false} during the movement phase. That is sufficient on Fabric, but Forge's FluidType
 * patches run the water-movement branch in {@code LivingEntity#travel} on
 * {@code isInWater() || isInFluidType(fluidstate)} -- so a holder is still treated as buoyant
 * even when {@code isInWater()} is forced false. Apoli's Connector integration is abandoned, so
 * instead we force the {@code isAffectedByFluids()} guard of that one branch to {@code false},
 * making the holder sink under normal gravity and walk the bottom (the "dense" dwarf behavior).
 *
 * <p>Crucially this targets ONLY the {@code isAffectedByFluids()} call inside {@code travel()}'s
 * water branch (ordinal 0 — the lava branch is ordinal 1), NOT the method itself. An earlier
 * version cancelled {@code isAffectedByFluids()} outright, but in {@code LivingEntity#aiStep} the
 * entire jump block (including the normal ground jump {@code jumpFromGround()}) is gated behind
 * {@code jumping && isAffectedByFluids()} -- so that broke jumping off the bottom too. Scoping the
 * override to {@code travel()} leaves the jump block intact; Apoli's client-side {@code isInWater()
 * == false} then steers it into the ground-jump path (floor hops work, jump-to-float does not),
 * matching vanilla {@code ignore_water}.
 *
 * <p>Guarded to water via Forge's FluidType height map ({@code isInFluidType(WATER_TYPE)}) rather
 * than {@code isInWater()}, because Apoli forces the latter false on the client. See
 * {@link IgnoreWaterPushMixin} for the current-push and swim-state halves.
 */
@Mixin(LivingEntity.class)
public abstract class IgnoreWaterMixin {

    @ModifyExpressionValue(
            method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isAffectedByFluids()Z", ordinal = 0))
    private boolean medievalorigins$ignoreWaterBuoyancy(boolean original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (original && !self.isInLava()
                && self.isInFluidType(ForgeMod.WATER_TYPE.get())
                && PowerHolderComponent.hasPower(self, IgnoreWaterPower.class)) {
            return false;
        }
        return original;
    }
}
