package dev.muon.medievalorigins.mixin.compat.apoli;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PhasingPower;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityPhasingMixin {

    @ModifyReturnValue(method = "isInWall", at = @At(value = "RETURN"))
    private boolean preventPhasingSuffocation(boolean original) {
        if (original) {
            return PowerHolderComponent.KEY.maybeGet(this)
                    .map(component -> component.getPowers(PhasingPower.class).stream().anyMatch(PhasingPower::isActive))
                    .map(isPhasing -> !isPhasing)
                    .orElse(true);
        }
        return false;
    }
}
