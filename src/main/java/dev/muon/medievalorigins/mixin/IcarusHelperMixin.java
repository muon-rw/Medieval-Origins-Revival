package dev.muon.medievalorigins.mixin;


import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.muon.medievalorigins.power.WingsPower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = IcarusHelper.class)
public abstract class IcarusHelperMixin {
    @Redirect(method = "onFallFlyingTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getFoodData()Lnet/minecraft/world/food/FoodData;"))
    private static FoodData redirectGetFoodData(Player player) {
        if (WingsPower.hasWingsPower(player)) {
            FoodData fakeFoodData = new FoodData();
            fakeFoodData.setFoodLevel(20);
            return fakeFoodData;
        }
        return player.getFoodData();
    }
}