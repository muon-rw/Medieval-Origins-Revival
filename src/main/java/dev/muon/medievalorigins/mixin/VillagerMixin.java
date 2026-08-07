package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.muon.medievalorigins.power.ModifyReputationPowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Villager.class)
public class VillagerMixin {

    @ModifyReturnValue(method = "getPlayerReputation", at = @At("RETURN"))
    private int modifyReputation(int original, @Local(argsOnly = true) Player player) {
        if (PowerHolderComponent.hasPowerType(player, ModifyReputationPowerType.class)) {
            List<ModifyReputationPowerType> powers = PowerHolderComponent.getPowerTypes(player, ModifyReputationPowerType.class)
                    .stream()
                    .filter(ModifyReputationPowerType::isActive)
                    .toList();

            double modified = original;
            for (ModifyReputationPowerType powerType : powers) {
                modified = ModifierUtil.applyModifiers(player, powerType.getModifiers(), modified);
            }

            return (int) modified;
        }
        return original;
    }
}
