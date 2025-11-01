package dev.muon.medievalorigins.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.muon.medievalorigins.power.FaeWingsPower;
import dev.muon.medievalorigins.power.PixieWingsPower;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    @Unique
    private static final int TICK_INTERVAL = 2;
    @Unique
    private static final double GROUND_DISTANCE_THRESHOLD = 0.1;

    public AbstractClientPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Player) this;
        if (player.tickCount % TICK_INTERVAL == 0) {
            boolean isPixie = PixieWingsPower.hasPower(player);
            boolean isFae = FaeWingsPower.hasPower(player);

            if (player.getAbilities().flying && isPixie) {
                player.elytraRotX = 1.4981317F;
                player.elytraRotY = 0.58726646F;
                player.elytraRotZ = (-0.5F - (float) Math.PI / 4F);
                return;
            } else if ((player.getAbilities().flying || player.hasEffect(MobEffects.LEVITATION)) && isFae) {
                player.elytraRotX = 1.4981317F;
                player.elytraRotY = 0.58726646F;
                player.elytraRotZ = (-0.5F - (float) Math.PI / 4F);
                return;
            }

            if (isFae || isPixie) {
                Vec3 playerMovement = player.getDeltaMovement();
                if (playerMovement.y < -0.5) return;
                double normalizedY = playerMovement.y;
                if (isFae) {
                    normalizedY = normalizedY * 2.5;
                }
                double speedMagnitude = Math.sqrt(playerMovement.x * playerMovement.x + playerMovement.z * playerMovement.z + Math.max(normalizedY, 0) * Math.max(normalizedY, 0)) * 4;
                float flapStrength = (float) Math.min(speedMagnitude, 1.0);

                Vec3 startPos = player.position();
                Vec3 endPos = startPos.add(0, -1, 0);
                HitResult hitResult = player.level().clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

                boolean isCloseToGround = hitResult.getType() != HitResult.Type.MISS && hitResult.getLocation().distanceTo(startPos) <= GROUND_DISTANCE_THRESHOLD;

                if (player.isFallFlying() || normalizedY > 0.1 || (!isCloseToGround && (Math.abs(playerMovement.x) + Math.abs(playerMovement.z) > 0.1))) {
                    player.elytraRotX = 1.4981317F * flapStrength;
                    player.elytraRotY = 0.58726646F * flapStrength;
                    player.elytraRotZ = (-0.5F - (float) Math.PI / 4F) * flapStrength;
                }
            }
        }
    }
}