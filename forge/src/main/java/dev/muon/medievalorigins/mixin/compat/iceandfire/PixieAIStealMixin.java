package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.github.alexthe666.iceandfire.entity.ai.PixieAISteal;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PixieAISteal.class)
public class PixieAIStealMixin {
    @Shadow private Player temptingPlayer;

    @ModifyReturnValue(method = "canUse", at = @At("RETURN"))
    private boolean preventPixieTheft(boolean original) {
        if (original && temptingPlayer != null) {
            return IOriginContainer.get(temptingPlayer)
                    .map(container -> container.getOrigins().values().stream()
                            .anyMatch(origin -> origin.location().equals(MedievalOrigins.loc("pixie"))))
                    .map(isPixie -> isPixie ? false : original)
                    .orElse(original);
        }
        return original;
    }
}