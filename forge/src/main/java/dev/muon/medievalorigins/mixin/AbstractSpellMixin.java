package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.action.CastSpellAction;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractSpell.class, remap = false)
public class AbstractSpellMixin {

    @Inject(method = "onServerCastTick", at = @At("HEAD"))
    private void onSpellTick(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, CallbackInfo ci) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CastSpellAction.onSpellTick(serverPlayer, playerMagicData);
        }
    }

    @Inject(method = "onServerCastComplete", at = @At("HEAD"))
    private void onSpellEnd(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled, CallbackInfo ci) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CastSpellAction.onSpellEnd(serverPlayer);
        }
    }
}