package dev.muon.medievalorigins.mixin;


import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.power.WingsPower;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = IcarusHelper.class)
public abstract class IcarusHelperMixin {
    private static final FoodData dummyFoodData = new FoodData();
    static {
        dummyFoodData.setFoodLevel(20);
    }

    @Redirect(method = "onFallFlyingTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getFoodData()Lnet/minecraft/world/food/FoodData;"))
    private static FoodData redirectGetFoodData(Player player) {
        if (WingsPower.hasWingsPower(player)) {
            return dummyFoodData;
        }
        return player.getFoodData();
    }
}