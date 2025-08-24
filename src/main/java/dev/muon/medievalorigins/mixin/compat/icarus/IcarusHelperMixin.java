package dev.muon.medievalorigins.mixin.compat.icarus;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cammiescorner.icarus.api.IcarusPlayerValues;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.power.IcarusWingsPowerType;
import dev.muon.medievalorigins.util.PowerCache;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;
import java.util.function.Predicate;


@Mixin(value = IcarusHelper.class)
public abstract class IcarusHelperMixin {

    @ModifyReturnValue(method = "getConfigValues", at = @At("RETURN"))
    private static IcarusPlayerValues modifyConfigValues(IcarusPlayerValues original, LivingEntity entity) {
        var icarusWings = PowerCache.getFirstPowerType(entity, IcarusWingsPowerType.class, PowerType::isActive);
        if (icarusWings.isEmpty()) return original;

        //TODO: Add as fields to power json
        return new IcarusPlayerValues() {
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
                return original.exhaustionAmount() / 4;
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

    @WrapOperation(method = "hasWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean hasWingsFromOrigin(Predicate<LivingEntity> instance, Object entity, Operation<Boolean> original) {
        return PowerCache.hasPowerType((LivingEntity) entity, IcarusWingsPowerType.class)
                || original.call(instance, entity);
    }

    @WrapOperation(method = "getEquippedWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object getOriginWings(Function<LivingEntity, ItemStack> instance, Object entity, Operation<ItemStack> original) {
        if (PowerCache.hasPowerType((LivingEntity) entity, IcarusWingsPowerType.class)) {
            return null;
        }
        return original.call(instance, entity);
    }
}