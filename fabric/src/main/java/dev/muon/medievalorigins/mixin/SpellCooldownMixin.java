package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.util.SpellCastingUtil;
import net.spell_engine.internals.SpellCooldownManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpellCooldownManager.class)
public class SpellCooldownMixin {
    @ModifyReturnValue(method = "isCoolingDown", at = @At(value = "RETURN"))
    private boolean bypassCooldown(boolean original) {
        if (SpellCastingUtil.bypassesCooldown()) {
            return false;
        } else {
            return original;
        }
    }
}
