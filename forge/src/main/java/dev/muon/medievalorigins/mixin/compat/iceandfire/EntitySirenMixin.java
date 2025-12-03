package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntitySiren.class, remap = false)
public class EntitySirenMixin {

    @ModifyReturnValue(method = "isWearingEarplugs", at = @At("RETURN"))
    private static boolean preventSirenCharm(boolean original, LivingEntity entity) {
        if (!original && entity instanceof Player player) {
            OriginComponent component = ModComponents.ORIGIN.get(player);
            return component.getOrigins().values().stream()
                    .anyMatch(origin -> origin.getIdentifier().equals(MedievalOrigins.loc("siren")));
        }
        return original;
    }
}



