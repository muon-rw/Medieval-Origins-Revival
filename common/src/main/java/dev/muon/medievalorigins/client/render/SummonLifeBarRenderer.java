package dev.muon.medievalorigins.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.muon.medievalorigins.entity.SummonedMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;

public class SummonLifeBarRenderer {

    public static void renderBar(PoseStack poseStack, MultiBufferSource bufferSource, Entity entity) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !isOwnerLookingAt(player, entity)) {
            return;
        }

        if (entity instanceof SummonedMob summon) {
            float percentage = (float) summon.getLifeTicks() / summon.getMaxLifeTicks();
            float scale = 0.04f;
            int barWidth = 17;
            int filledWidth = (int)(barWidth * percentage);

            poseStack.pushPose();

            poseStack.translate(0, entity.getBbHeight() + 0.4f, 0);
            var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            poseStack.mulPose(camera.rotation());
            poseStack.scale(scale, scale, scale);

            float x = -barWidth / 2.0f;
            float y = 0;

            var buffer = bufferSource.getBuffer(RenderType.guiOverlay());
            Matrix4f matrix = poseStack.last().pose();

            // Background (black)
            buffer.addVertex(matrix, x, y, 0)
                    .setColor(0, 0, 0, 127);
            buffer.addVertex(matrix, x + barWidth, y, 0)
                    .setColor(0, 0, 0, 127);
            buffer.addVertex(matrix, x + barWidth, y + 1, 0)
                    .setColor(0, 0, 0, 127);
            buffer.addVertex(matrix, x, y + 1, 0)
                    .setColor(0, 0, 0, 127);

            // Progress bar (green to red)
            if (filledWidth > 0) {
                int green = percentage > 0.5f ? 255 : (int)(255 * percentage * 2);
                int red = percentage > 0.5f ? (int)(255 * (1 - percentage) * 2) : 255;

                buffer.addVertex(matrix, x, y, 0)
                        .setColor(red, green, 0, 200);
                buffer.addVertex(matrix, x + filledWidth, y, 0)
                        .setColor(red, green, 0, 200);
                buffer.addVertex(matrix, x + filledWidth, y + 1, 0)
                        .setColor(red, green, 0, 200);
                buffer.addVertex(matrix, x, y + 1, 0)
                        .setColor(red, green, 0, 200);
            }

            poseStack.popPose();
        }
    }

    private static boolean isOwnerLookingAt(Player player, Entity entity) {
        if (!(entity instanceof SummonedMob summon)) {
            return false;
        }

        if (!player.getUUID().equals(summon.getOwnerUUID())) {
            return false;
        }

        var hitResult = Minecraft.getInstance().hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
            return false;
        }

        return ((EntityHitResult) hitResult).getEntity() == entity;
    }
}