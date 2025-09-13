package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.util.PowerCache;
import io.github.apace100.apoli.power.type.IgnoreWaterPowerType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class IgnoreWaterPowerTypeMixin {

    @ModifyReturnValue(method = "isInWater", at = @At("RETURN"))
    private boolean medievalOrigins$ignoreWater(boolean original) {
        Entity ths = (Entity) (Object) this;
        if (original && ths instanceof Player) {
            if (PowerCache.hasPowerType(ths, IgnoreWaterPowerType.class)) {
                return false;
            }
        }
        return original;
    }
}
