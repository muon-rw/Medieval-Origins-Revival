package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.muon.medievalorigins.action.CastSpellAction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpellHelper.class)
public class SpellHelperMixin {
    @WrapOperation(
            method = "performSpell",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/spell_engine/internals/SpellHelper;ammoForSpell(Lnet/minecraft/world/entity/player/Player;Lnet/spell_engine/api/spell/Spell;Lnet/minecraft/world/item/ItemStack;)Lnet/spell_engine/internals/SpellHelper$AmmoResult;"
            )
    )
    private static SpellHelper.AmmoResult bypassAmmoCheck(Player player, Spell spell, ItemStack itemStack, Operation<SpellHelper.AmmoResult> original) {
        if (!CastSpellAction.requiresAmmo()) {
            return new SpellHelper.AmmoResult(true, itemStack);
        } else {
            return original.call(player, spell, itemStack);
        }
    }
}