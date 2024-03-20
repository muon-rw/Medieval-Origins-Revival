package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.power.CastFreelyPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(SpellHelper.class)
public abstract class SpellHelperMixin {
    @Inject(method = "ammoForSpell", at = @At("HEAD"), cancellable = true)
    private static void checkForCastFreelyPower(Player player, Spell spell, ItemStack itemStack, CallbackInfoReturnable<SpellHelper.AmmoResult> cir) {
        if (PowerHolderComponent.hasPower(player, CastFreelyPower.class)) {
            cir.setReturnValue(new SpellHelper.AmmoResult(true, null));
        }
    }
}