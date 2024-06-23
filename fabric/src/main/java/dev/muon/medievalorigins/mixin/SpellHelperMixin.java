package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.muon.medievalorigins.action.CastSpellAction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpellHelper.class)
public class SpellHelperMixin {
    @WrapOperation(
            method = "ammoForSpell",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;contains(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private static boolean bypassAmmoCheck(Inventory instance, ItemStack $$0, Operation<Boolean> original) {
        if (!CastSpellAction.requiresAmmo()) {
            return true;
        } else {
            return original.call(instance, $$0);
        }
    }
}