package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.muon.medievalorigins.power.ModifyInsomniaTicksPower;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerMixin {

    // Vanilla clamps this read to at least 1 before rolling, so a modified 0 still means "never spawn"
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/ServerStatsCounter;getValue(Lnet/minecraft/stats/Stat;)I"))
    private int modifyTicksSinceRest(int ticksSinceRest, @Local ServerPlayer player) {
        return ModifyInsomniaTicksPower.modify(player, ticksSinceRest);
    }
}
