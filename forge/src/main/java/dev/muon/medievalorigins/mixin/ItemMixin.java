package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.power.EdibleItemPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Item.class, priority = 1500)
public class ItemMixin {

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEdible()Z"))
    private boolean makeEdible(boolean original, Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return original || PowerHolderComponent.hasPower(player, EdibleItemPower.class, power -> power.doesApply(level, stack));
    }

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodProperties;canAlwaysEat()Z"))
    private boolean makeEdibleWhenPlayerFull(boolean original, Level level, Player player, InteractionHand hand) {
        return original || PowerHolderComponent.hasPower(player, EdibleItemPower.class, power -> power.getFoodComponent() != null && power.getFoodComponent().canAlwaysEat());
    }

    @ModifyExpressionValue(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
    private boolean triggerEat(boolean original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) Level level) {
        InteractionHand hand = entity.getUsedItemHand();
        return original || PowerHolderComponent.hasPower(entity, EdibleItemPower.class, power -> power.doesApply(level, entity.getItemInHand(hand)));
    }

    // This target isn't useable on forge and there's no alternative, but it should be covered by the coremod
    // Leaving it in as it doesn't cause compile errors
    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"), remap = false)
    private FoodProperties injectCustomFoodProperties(ItemStack instance, LivingEntity livingEntity, Operation<FoodProperties> original, @Local(argsOnly = true) Player player, @Local(argsOnly = true) Level level, @Local(argsOnly = true) InteractionHand hand) {
        FoodProperties originalFood = original.call(instance, livingEntity);
        if (originalFood != null) {
            return originalFood;
        }

        ItemStack stack = player.getItemInHand(hand);
        for (EdibleItemPower power : PowerHolderComponent.getPowers(player, EdibleItemPower.class)) {
            if (power.doesApply(level, stack)) {
                return power.getFoodComponent();
            }
        }
        return null;
    }
}

