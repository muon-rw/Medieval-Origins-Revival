package dev.muon.medievalorigins.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.muon.medievalorigins.power.EdibleItemPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FoodData.class, priority = 1500)
public class FoodDataMixin {

    @ModifyExpressionValue(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
    private boolean accountCustomFoodWhenEating(boolean original, Item item, ItemStack stack, LivingEntity entity) {
        return original || PowerHolderComponent.hasPower(entity, EdibleItemPower.class, power -> power.doesApply(entity.level(), stack));
    }

    // Have to target this one with a coremod into IForgeItemStack
//    @ModifyExpressionValue(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getFoodProperties()Lnet/minecraft/world/food/FoodProperties;"))
//    private FoodProperties replaceWithCustomFood(FoodProperties original, Item item, ItemStack stack, LivingEntity entity) {
//        for (EdibleItemPower power : PowerHolderComponent.getPowers(cachedPlayer, EdibleItemPower.class)) {
//            if (power.doesApply(cachedPlayer.level(), stack)) {
//                return power.getFoodComponent();
//            }
//        }
//        return original;
//    }
}
