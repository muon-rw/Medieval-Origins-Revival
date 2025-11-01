package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.muon.medievalorigins.power.ModifyReputationPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
public class VillagerMixin {

    @ModifyReturnValue(method = "getPlayerReputation", at = @At("RETURN"))
    private int medievalorigins$modifyReputation(int original, @Local(argsOnly = true) Player player) {
        float modified = PowerHolderComponent.modify(player, ModifyReputationPower.class, (float) original);
        return Math.round(modified);
    }
}

