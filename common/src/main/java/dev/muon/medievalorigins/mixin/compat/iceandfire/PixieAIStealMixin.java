package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.iafenvoy.iceandfire.entity.ai.PixieAIStealGoal;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.util.OriginUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PixieAIStealGoal.class, remap = false)
public class PixieAIStealMixin {
    @Shadow(remap = false) private Player temptingPlayer;

    @Unique
    private static final ResourceLocation PIXIE_ORIGIN_ID = MedievalOrigins.loc("pixie");

    @ModifyReturnValue(method = "canUse", at = @At("RETURN"), remap = true)
    private boolean preventPixieTheft(boolean original) {
        if (original && temptingPlayer != null) {
            if (OriginUtil.hasOrigin(temptingPlayer, PIXIE_ORIGIN_ID)) {
                return false;
            }
        }
        return original;
    }
}