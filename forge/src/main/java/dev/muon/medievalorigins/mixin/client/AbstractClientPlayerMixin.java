package dev.muon.medievalorigins.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.muon.medievalorigins.power.PixieWingsPower;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    private static final int TICK_INTERVAL = 2;

    public AbstractClientPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Player) this;
        if (player.tickCount % TICK_INTERVAL == 0 && PixieWingsPower.hasPower(player)) {
            if (player.isFallFlying()) {
                player.elytraRotX = 0.6981317F;
                player.elytraRotY = 0.08726646F;
                player.elytraRotZ = -(float)Math.PI / 4F;
                return;
            }
            if (player.getDeltaMovement().y() > 0) {
                player.elytraRotX = 0.6981317F;
                player.elytraRotY = 0.08726646F;
                player.elytraRotZ = -(float)Math.PI / 4F;
                return;
            }
        }
    }
}