package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.muon.medievalorigins.attribute.ModAttributes;
import dev.muon.medievalorigins.power.*;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
                damageAmount += (float) attacker.getAttributeValue(ModAttributes.SUMMON_RANGED_DAMAGE);
            }
        }

        return damageAmount;
    }


    @ModifyExpressionValue(method = "shouldTriggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I"))
    private int modifyUseDuration(int original) {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack useItem = self.getItemInHand(self.getUsedItemHand());
        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), useItem)) {
                FoodProperties foodProperties = power.getFoodComponent();
                return foodProperties != null && foodProperties.isFastFood() ? 16 : 32;
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "shouldTriggerItemUseEffects", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"), remap = false)
    private FoodProperties modifyGetFoodProperties(FoodProperties original) {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack useItem = self.getItemInHand(self.getUsedItemHand());
        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), useItem)) {
                return power.getFoodComponent();
            }
        }
        return original;
    }

    // Handle getUseDuration in shouldTriggerItemUseEffects
    @WrapOperation(
            method = "shouldTriggerItemUseEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I")
    )
    private int medievalorigins$customUseDurationInShouldTrigger(ItemStack stack, Operation<Integer> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        int originalDuration = original.call(stack);

        if (originalDuration == 0) {
            for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
                if (power.doesApply(self.level(), stack)) {
                    FoodProperties foodProps = power.getFoodComponent();
                    if (foodProps != null) {
                        return foodProps.isFastFood() ? 16 : 32;
                    }
                }
            }
        }

        return originalDuration;
    }

    @WrapOperation(
            method = "triggerItemUseEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;")
    )
    private UseAnim medievalorigins$modifyUseAnimation(ItemStack stack, Operation<UseAnim> original) {
        LivingEntity self = (LivingEntity) (Object) this;

        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), stack)) {
                UseAnim customAnim = power.getUseAction();
                if (customAnim != null && customAnim != UseAnim.NONE) {
                    return customAnim;
                }
            }
        }

        return original.call(stack);
    }

    @WrapOperation(
            method = "triggerItemUseEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEatingSound(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/sounds/SoundEvent;")
    )
    private SoundEvent medievalorigins$customEatingTickSound(LivingEntity instance, ItemStack stack, Operation<SoundEvent> original) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Check for custom sound from edible item power during eating ticks
        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), stack)) {
                SoundEvent customSound = power.getSound();
                if (customSound != null) {
                    return customSound;
                }
            }
        }

        return original.call(instance, stack);
    }

    @WrapOperation(
            method = "triggerItemUseEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDrinkingSound(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/sounds/SoundEvent;")
    )
    private SoundEvent medievalorigins$customDrinkingTickSound(LivingEntity instance, ItemStack stack, Operation<SoundEvent> original) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Check for custom sound from edible item power during drinking ticks
        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), stack)) {
                SoundEvent customSound = power.getSound();
                if (customSound != null) {
                    return customSound;
                }
            }
        }

        return original.call(instance, stack);
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void medievalorigins$completeUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack useItem = self.getItemInHand(self.getUsedItemHand());

        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), useItem)) {
                // Execute entity actions
                if (power.entityActionWhenEaten != null) {
                    power.entityActionWhenEaten.accept(self);
                }

                // Execute item actions
                if (power.itemActionWhenEaten != null) {
                    power.itemActionWhenEaten.accept(new Tuple<>(self.level(), useItem));
                }

                // Handle return stack
                ItemStack returnStack = power.getReturnStack();
                if (returnStack != null && !returnStack.isEmpty()) {
                    if (self instanceof Player player) {
                        if (!player.getInventory().add(returnStack.copy())) {
                            player.drop(returnStack.copy(), false);
                        }
                    }
                }
            }
        }
    }

    // Inject into eat method to handle custom sounds and edible items
    @ModifyExpressionValue(
            method = "eat",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEdible()Z")
    )
    private boolean medievalorigins$isEdibleInEat(boolean original, @Local(argsOnly = true) ItemStack food) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!original) {
            for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
                if (power.doesApply(self.level(), food)) {
                    return true;
                }
            }
        }
        return original;
    }

    @WrapOperation(
            method = "eat",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEatingSound(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/sounds/SoundEvent;")
    )
    private SoundEvent medievalorigins$customEatingSound(LivingEntity instance, ItemStack food, Operation<SoundEvent> original) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Check for custom sound from edible item power
        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), food)) {
                SoundEvent customSound = power.getSound();
                if (customSound != null) {
                    return customSound;
                }
            }
        }

        return original.call(instance, food);
    }

    // For some reason, MixinExtras thinks this LivingEntity local doesn't exist
    @ModifyExpressionValue(
            method = "addEatEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z")
    )
    private boolean medievalorigins$isEdibleInAddEatEffect(boolean original, @Local(argsOnly = true) ItemStack food, @Local(argsOnly = true) LivingEntity livingEntity) {
        if (!original) {
            for (EdibleItemPower power : PowerHolderComponent.getPowers(livingEntity, EdibleItemPower.class)) {
                if (power.doesApply(livingEntity.level(), food)) {
                    return true;
                }
            }
        }
        return original;
    }

    // For some reason, MixinExtras thinks this LivingEntity local doesn't exist
    @WrapOperation(
            method = "addEatEffect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;", remap = false)
    )
    private FoodProperties medievalorigins$customFoodPropertiesInAddEatEffect(ItemStack instance, LivingEntity livingEntity, Operation<FoodProperties> original, @Local(argsOnly = true) ItemStack food) {
        FoodProperties originalFood = original.call(instance, livingEntity);
        if (originalFood != null) {
            return originalFood;
        }

        // Check for custom food properties from edible item power
        for (EdibleItemPower power : PowerHolderComponent.getPowers(livingEntity, EdibleItemPower.class)) {
            if (power.doesApply(livingEntity.level(), food)) {
                return power.getFoodComponent();
            }
        }

        return null;
    }

    // Sync power to client when starting to use an edible item
    @Inject(method = "startUsingItem", at = @At("TAIL"))
    private void medievalorigins$syncEdiblePowerOnStart(InteractionHand hand, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) return;

        ItemStack stack = self.getItemInHand(hand);
        for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
            if (power.doesApply(self.level(), stack)) {
                PowerHolderComponent.syncPower(self, power.getType());
                return;
            }
        }
    }

    // Handle getUseDuration in startUsingItem
    @WrapOperation(
            method = "startUsingItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I")
    )
    private int medievalorigins$customUseDurationInStart(ItemStack stack, Operation<Integer> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        int originalDuration = original.call(stack);

        if (originalDuration == 0) {
            // Check for edible item power
            for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
                if (power.doesApply(self.level(), stack)) {
                    FoodProperties foodProps = power.getFoodComponent();
                    if (foodProps != null) {
                        return foodProps.isFastFood() ? 16 : 32;
                    }
                }
            }
        }

        return originalDuration;
    }

    // Handle getUseDuration in onSyncedDataUpdated
    @WrapOperation(
            method = "onSyncedDataUpdated",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I")
    )
    private int medievalorigins$customUseDurationInSync(ItemStack stack, Operation<Integer> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        int originalDuration = original.call(stack);

        if (originalDuration == 0) {
            // Check for edible item power
            for (EdibleItemPower power : PowerHolderComponent.getPowers(self, EdibleItemPower.class)) {
                if (power.doesApply(self.level(), stack)) {
                    FoodProperties foodProps = power.getFoodComponent();
                    if (foodProps != null) {
                        return foodProps.isFastFood() ? 16 : 32;
                    }
                }
            }
        }

        return originalDuration;
    }

    @Unique
    private boolean medievalorigins$shouldIgnoreTarget(Player player) {
        LivingEntity self = (LivingEntity) (Object) this;
        return PowerHolderComponent.getPowers(player, MobsIgnorePower.class).stream()
                .filter(MobsIgnorePower::isActive)
                .anyMatch(power -> power.shouldIgnore(self, player));
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void medievalorigins$preventAttackValidation(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && medievalorigins$shouldIgnoreTarget(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void medievalorigins$invokeTargetDeathAction(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            LivingEntity self = (LivingEntity) (Object) this;
            PowerHolderComponent.getPowers(attacker, ActionOnTargetDeathPower.class).stream()
                    .filter(power -> power.doesApply(self, source, amount))
                    .forEach(power -> power.executeActions(self, source, amount));
        }
    }

    @WrapOperation(method = "handleEntityEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V",
            ordinal = 0))
    private void medievalorigins$onDeathSound(LivingEntity instance, SoundEvent soundEvent, float volume, float pitch, Operation<Void> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        var powers = PowerHolderComponent.getPowers(self, CustomDeathSoundPower.class);
        if (!powers.isEmpty()) {
            boolean anyMuted = powers.stream().anyMatch(CustomDeathSoundPower::isMuted);
            if (!anyMuted) {
                powers.forEach(power -> power.playDeathSound(self));
            }
        } else {
            original.call(instance, soundEvent, volume, pitch);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void medievalorigins$onJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        PowerHolderComponent.getPowers(self, ActionOnJumpPower.class)
                .forEach(power -> power.executeAction(self));
    }

}
