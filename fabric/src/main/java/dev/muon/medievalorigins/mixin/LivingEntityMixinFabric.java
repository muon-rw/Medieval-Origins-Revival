package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.attribute.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixinFabric {
    @ModifyReturnValue(method = "createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", at = @At("RETURN"))
    private static AttributeSupplier.Builder addAttributes(AttributeSupplier.Builder original) {
        original.add(ModAttributes.PROJECTILE_DAMAGE_BONUS);
        return original;
    }
}
