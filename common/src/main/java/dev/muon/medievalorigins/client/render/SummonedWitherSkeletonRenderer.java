package dev.muon.medievalorigins.client.render;

import dev.muon.medievalorigins.entity.SummonedWitherSkeleton;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class SummonedWitherSkeletonRenderer extends HumanoidMobRenderer<SummonedWitherSkeleton, SkeletonModel<SummonedWitherSkeleton>> {
    private static final ResourceLocation WITHER_SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png");

    public SummonedWitherSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.WITHER_SKELETON)), 0.7F);

        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new SkeletonModel<>(context.bakeLayer(ModelLayers.WITHER_SKELETON_INNER_ARMOR)),
                new SkeletonModel<>(context.bakeLayer(ModelLayers.WITHER_SKELETON_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(SummonedWitherSkeleton entity) {
        return WITHER_SKELETON_LOCATION;
    }
}