package dev.muon.medievalorigins.mixin.compat.icarus;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.cammiescorner.icarus.api.IcarusPlayerValues;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.compat.icarus.IcarusWingsCompat;
import dev.muon.medievalorigins.power.IcarusWingsPower;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = IcarusHelper.class, remap = false)
public abstract class IcarusHelperMixin {

    @ModifyReturnValue(method = "getConfigValues", at = @At("RETURN"))
    private static IcarusPlayerValues applyWingsPowerOverrides(IcarusPlayerValues original, LivingEntity entity) {
        IcarusWingsCompat.installHooks();
        IcarusWingsPower.Config cfg = IcarusWingsPower.get(entity);
        return cfg == null ? original : IcarusWingsCompat.withOverrides(cfg, original);
    }
}
