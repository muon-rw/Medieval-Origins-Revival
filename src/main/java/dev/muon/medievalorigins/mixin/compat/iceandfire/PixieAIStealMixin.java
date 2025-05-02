package dev.muon.medievalorigins.mixin.compat.iceandfire;
import com.iafenvoy.iceandfire.entity.ai.PixieAISteal;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PixieAISteal.class, remap = false)
public class PixieAIStealMixin {
    @Shadow(remap = false) private Player temptingPlayer;

    @Unique
    private static final ResourceLocation PIXIE_ORIGIN_ID = MedievalOrigins.loc("pixie");

    @ModifyReturnValue(method = "canUse", at = @At("RETURN"), remap = true)
    private boolean preventPixieTheft(boolean original) {
        if (original && temptingPlayer != null) {
            OriginComponent originComponent = ModComponents.ORIGIN.get(temptingPlayer);
            for (Origin playerOrigin : originComponent.getOrigins().values()) {
                if (playerOrigin != null && playerOrigin.getId().equals(PIXIE_ORIGIN_ID)) {
                    return false;
                }
            }
        }
        return original;
    }
}