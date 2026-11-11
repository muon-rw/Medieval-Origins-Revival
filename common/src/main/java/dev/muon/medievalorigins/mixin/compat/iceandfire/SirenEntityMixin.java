package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.iafenvoy.iceandfire.entity.SirenEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.util.OriginUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SirenEntity.class, remap = false)
public abstract class SirenEntityMixin {
    @Unique
    private static final ResourceLocation SIREN_ORIGIN_ID = MedievalOrigins.loc("siren");

    @ModifyReturnValue(method = "isWearingEarplugs", at = @At("RETURN"))
    private static boolean preventCharmOnSirenOrigin(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        return original || entity instanceof Player player && OriginUtil.hasOrigin(player, SIREN_ORIGIN_ID);
    }
}
