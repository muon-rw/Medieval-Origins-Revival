package dev.muon.medievalorigins.mixin;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.muon.medievalorigins.power.EdibleItemPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
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

    // Not needed for the vanilla logic chain due to mixins into Item/LivingEntity, but just covering all the bases in case food mods interact weirdly
    // Might have a performance overhead, if so it can probably be removed without issue
    @Unique
    private Player cachedPlayer;

    @Inject(method = "tick", at = @At("HEAD"))
    private void cachePlayer(Player player, CallbackInfo ci) {
        this.cachedPlayer = player;
    }

    @ModifyExpressionValue(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
    private boolean accountCustomFoodWhenEating(boolean original, Item item, ItemStack stack) {
        return original || PowerHolderComponent.hasPower(cachedPlayer, EdibleItemPower.class, power -> power.doesApply(cachedPlayer.level(), stack));
    }

    @ModifyExpressionValue(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getFoodProperties()Lnet/minecraft/world/food/FoodProperties;"))
    private FoodProperties replaceWithCustomFood(FoodProperties original, Item item, ItemStack stack) {
        for (EdibleItemPower power : PowerHolderComponent.getPowers(cachedPlayer, EdibleItemPower.class)) {
            if (power.doesApply(cachedPlayer.level(), stack)) {
                return power.getFoodComponent();
            }
        }
        return original;
    }
}
