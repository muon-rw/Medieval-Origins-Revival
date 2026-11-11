package dev.muon.medievalorigins.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.muon.medievalorigins.client.render.SummonLifeBarRenderer;
import dev.muon.medievalorigins.entity.SummonedMob;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderLifeBar(LivingEntity entity, float yaw, float partialTick,
                               PoseStack poseStack, MultiBufferSource buffer,
                               int packedLight, CallbackInfo ci) {
        if (entity instanceof SummonedMob summon && summon.isLimitedLife()) {
            SummonLifeBarRenderer.renderBar(poseStack, buffer, entity);
        }
    }
}