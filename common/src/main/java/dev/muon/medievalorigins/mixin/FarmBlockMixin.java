package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.PreventCropTramplePower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void preventTrampling(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (entity != null && PreventCropTramplePower.isActive(entity)) {
            ci.cancel();
        }
    }
}
