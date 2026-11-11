package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.power.ModifyReputationPower;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
public class VillagerMixin {

    @ModifyReturnValue(method = "getPlayerReputation", at = @At("RETURN"))
    private int modifyReputation(int original, Player player) {
        return ModifyReputationPower.modify(player, original);
    }
}
