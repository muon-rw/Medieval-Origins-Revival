package dev.muon.medievalorigins.mixin;

import dev.muon.medievalorigins.attribute.ModAttributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    @Unique
    private static final TagKey<DamageType> MAGIC_DAMAGE = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation("medievalorigins", "is_magic")
    );


    @ModifyVariable(
            method = "hurt",
            at = @At("HEAD"),
            argsOnly = true,
            index = 2
    )
    private float modifyDamageAmount(float damageAmount, DamageSource damageSource) {
        if (damageAmount <= 0) {
            return damageAmount;
        }

        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            if (damageSource.getDirectEntity() instanceof Projectile || damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
                damageAmount += (float) attacker.getAttributeValue(ModAttributes.PROJECTILE_DAMAGE_BONUS);
            }
        }

        return damageAmount;
    }
}
