package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.util.PowerCache;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.apoli.power.Power;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PowerHolderComponentImpl.class, remap = false)
public abstract class PowerHolderComponentImplMixin {
    
    @Shadow @Final
    private LivingEntity owner;
    
    @Inject(method = "addPower", at = @At("RETURN"))
    private void onAddPower(Power power, ResourceLocation source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            // Power was successfully added, invalidate cache
            PowerCache.invalidate(owner);
        }
    }
    
    @Inject(method = "removePower", at = @At("RETURN"))
    private void onRemovePower(Power power, ResourceLocation source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            // Power was successfully removed, invalidate cache
            PowerCache.invalidate(owner);
        }
    }
    
    @Inject(method = "removeAllPowersFromSource", at = @At("RETURN"))
    private void onRemoveAllPowers(ResourceLocation source, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() > 0) {
            // Powers were removed, invalidate cache
            PowerCache.invalidate(owner);
        }
    }
}
