package dev.muon.medievalorigins.client.render;

import dev.muon.medievalorigins.entity.SummonedArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SummonedArrowRenderer extends ArrowRenderer<SummonedArrow> {
    public static final ResourceLocation NORMAL_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
    public static final ResourceLocation TIPPED_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/tipped_arrow.png");

    public SummonedArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SummonedArrow entity) {
        return entity.getColor() > 0 ? TIPPED_ARROW_LOCATION : NORMAL_ARROW_LOCATION;
    }
}