package dev.muon.medievalorigins.mixin.compat.apoli;

import dev.muon.medievalorigins.util.HarvestContextStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ActionOnBlockBreakPower;
import io.github.apace100.apoli.util.HarvestContext;
import io.github.apace100.apoli.util.SavedBlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces Apoli's ServerPlayerInteractionManagerMixin to fix NPE when radial/vein mining
 * mods trigger nested block breaks. Uses a stack to preserve context across nested calls.
 */
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow
    protected ServerLevel level;

    @Final
    @Shadow
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void medievalorigins$cacheBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // Push current context onto stack before it gets overwritten
        HarvestContextStack.pushCurrentContext();
        
        // Set new context for this block
        HarvestContext.setBlockPosition(this.level, pos);
        HarvestContext.setCanHarvest(this.player.hasCorrectToolForDrops(this.level.getBlockState(pos)));
    }

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void medievalorigins$onBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            Boolean canHarvest = HarvestContext.getCanHarvest();
            boolean harvested = canHarvest != null ? canHarvest : false;
            SavedBlockPosition blockPosition = HarvestContext.getBlockPosition();
            
            if (blockPosition != null) {
                PowerHolderComponent.getPowers(this.player, ActionOnBlockBreakPower.class)
                        .stream()
                        .filter(p -> p.doesApply(blockPosition))
                        .forEach(p -> p.executeActions(harvested, pos, (Direction) null));
            }
        }
        
        // Clear current context
        HarvestContext.clearCanHarvest();
        HarvestContext.clearBlockPosition();
        
        // Restore previous context from stack if there was a nested call
        HarvestContextStack.popAndRestoreContext();
    }
}
