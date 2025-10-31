package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.github.alexthe666.iceandfire.entity.ai.PixieAISteal;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PixieAISteal.class, remap = false)
public class PixieAIStealMixin {
    @Shadow(remap = false) private Player temptingPlayer;

    @ModifyReturnValue(method = "canUse", at = @At("RETURN"))
    private boolean preventPixieTheft(boolean original) {
        if (original && temptingPlayer != null) {
            OriginComponent component = ModComponents.ORIGIN.get(temptingPlayer);
            boolean isPixie = component.getOrigins().values().stream()
                    .anyMatch(origin -> origin.getIdentifier().equals(MedievalOrigins.loc("pixie")));
            return !isPixie;
        }
        return original;
    }
}
