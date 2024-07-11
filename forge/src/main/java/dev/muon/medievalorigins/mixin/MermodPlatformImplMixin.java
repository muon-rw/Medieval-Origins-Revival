package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.power.MermodTailPower;
import io.github.thatpreston.mermod.client.render.TailStyle;
import io.github.thatpreston.mermod.forge.MermodPlatformImpl;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MermodPlatformImpl.class, remap = false)
public class MermodPlatformImplMixin {

    @ModifyReturnValue(method = "getTailStyle", at = @At("RETURN"))
    private static TailStyle injectPowerTailStyle(TailStyle original, Player player) {
        MermodTailPower tailPower = MermodTailPower.getTailStyle(player);
        if (tailPower != null) {
            return new TailStyle(
                    tailPower.getTexture(),
                    tailPower.getModel(),
                    tailPower.getTailColor(),
                    tailPower.hasBra(),
                    tailPower.getBraColor(),
                    tailPower.hasGradient(),
                    tailPower.getGradientColor(),
                    tailPower.hasGlint(),
                    tailPower.isPermanent()
            );
        }
        return original;
    }

    @ModifyReturnValue(method = "hasTailStyle", at = @At("RETURN"))
    private static boolean hasTailStyleFromPower(boolean original, Player player) {
        if (MermodTailPower.hasPower(player)) return true;
        return original;
    }
}