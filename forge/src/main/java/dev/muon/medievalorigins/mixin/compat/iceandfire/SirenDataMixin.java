package dev.muon.medievalorigins.mixin.compat.iceandfire;

/*
import com.github.alexthe666.iceandfire.entity.props.SirenData;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SirenData.class, remap = false)
public class SirenDataMixin {

    @Inject(method = "setCharmed", at = @At("HEAD"), cancellable = true)
    private void preventSirenCharm(Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            OriginComponent component = ModComponents.ORIGIN.get(player);
            boolean isSiren = component.getOrigins().values().stream()
                    .anyMatch(origin -> origin.getIdentifier().equals(MedievalOrigins.loc("siren")));
            if (isSiren) {
                ci.cancel();
            }
        }
    }
}

 */