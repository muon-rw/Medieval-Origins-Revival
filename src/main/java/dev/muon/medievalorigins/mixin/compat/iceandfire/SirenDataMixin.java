package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.iafenvoy.iceandfire.data.component.SirenData;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SirenData.class, remap = false)
public abstract class SirenDataMixin {

    @Shadow public abstract void clearCharm();

    @Unique
    private static final ResourceLocation SIREN_ORIGIN_ID = MedievalOrigins.loc("siren");

    @Inject(method = "tick(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private void medievalorigins$preventSirenCharmOnSirenOrigin(LivingEntity holder, CallbackInfo ci) {
        if (holder instanceof Player player) {
            OriginComponent component = ModComponents.ORIGIN.get(player);
            if (component.getOrigins().values().stream()
                    .anyMatch(origin -> origin != null && origin.getId().equals(SIREN_ORIGIN_ID))) {
                this.clearCharm();
                ci.cancel();
            }
        }
    }
}
