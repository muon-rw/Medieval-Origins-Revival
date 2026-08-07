package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.IgnoreWaterPowerType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class IgnoreWaterPowerTypeMixin {

    // Probably only need one of these

    @ModifyReturnValue(method = "isInWater", at = @At("RETURN"))
    private boolean medievalOrigins$modifyInWater(boolean original) {
        Entity ths = (Entity) (Object) this;
        if (original && ths instanceof Player) {
            boolean hasActivePower = PowerHolderComponent.hasPowerType(ths, IgnoreWaterPowerType.class);
            if (hasActivePower) {
                return false;
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "isUnderWater", at = @At("RETURN"))
    private boolean medievalOrigins$modifyUnderWater(boolean original) {
        Entity ths = (Entity) (Object) this;
        if (original && ths instanceof Player) {
            boolean hasActivePower = PowerHolderComponent.hasPowerType(ths, IgnoreWaterPowerType.class);
            if (hasActivePower) {
                return false;
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "isSwimming", at = @At("RETURN"))
    private boolean medievalOrigins$modifySwimming(boolean original) {
        Entity ths = (Entity) (Object) this;
        if (original && ths instanceof Player) {
            boolean hasActivePower = PowerHolderComponent.hasPowerType(ths, IgnoreWaterPowerType.class);
            if (hasActivePower) {
                return false;
            }
        }
        return original;
    }
}
