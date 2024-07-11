package dev.muon.medievalorigins.mixin;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cammiescorner.icarus.api.IcarusPlayerValues;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.power.IcarusWingsPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.function.Predicate;


@Mixin(value = IcarusHelper.class)
public abstract class IcarusHelperMixin {
    @ModifyReturnValue(method = "getConfigValues", at = @At("RETURN"))
    private static IcarusPlayerValues modifyConfigValues(IcarusPlayerValues original, LivingEntity entity) {
        if (IcarusWingsPower.hasPower(entity)) {
            return new IcarusPlayerValues() {
                //todo: add these as fields to power json
                @Override
                public float wingsSpeed() {
                    return original.wingsSpeed();
                }

                @Override
                public float maxSlowedMultiplier() {
                    return original.maxSlowedMultiplier();
                }

                @Override
                public boolean armorSlows() {
                    return original.armorSlows();
                }

                @Override
                public boolean canLoopDeLoop() {
                    return original.canLoopDeLoop();
                }

                @Override
                public boolean canSlowFall() {
                    return original.canSlowFall();
                }

                @Override
                public float exhaustionAmount() {
                    return original.exhaustionAmount()/4;
                }

                @Override
                public int maxHeightAboveWorld() {
                    return original.maxHeightAboveWorld();
                }

                @Override
                public boolean maxHeightEnabled() {
                    return original.maxHeightEnabled();
                }

                @Override
                public float requiredFoodAmount() {
                    return 0;
                }
            };
        }
        return original;
    }

    @WrapOperation(method = "hasWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean hasWingsFromOrigin(Predicate<LivingEntity> instance, Object entity, Operation<Boolean> original) {
        return IcarusWingsPower.hasPower((LivingEntity) entity) || original.call(instance, entity);
    }

    @WrapOperation(method = "getEquippedWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object getOriginWings(Function<LivingEntity, ItemStack> instance, Object entity, Operation<ItemStack> original) {
        if (IcarusWingsPower.hasPower((LivingEntity) entity)) {
            return null;
        }
        return original.call(instance, entity);
    }
}