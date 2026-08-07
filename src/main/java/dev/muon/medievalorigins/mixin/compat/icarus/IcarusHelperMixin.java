package dev.muon.medievalorigins.mixin.compat.icarus;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cammiescorner.icarus.api.IcarusPlayerValues;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.power.IcarusWingsPowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


@Mixin(value = IcarusHelper.class)
public abstract class IcarusHelperMixin {

    @ModifyReturnValue(method = "getConfigValues", at = @At("RETURN"))
    private static IcarusPlayerValues modifyConfigValues(IcarusPlayerValues original, LivingEntity entity) {
        List<IcarusWingsPowerType> icarusWings = PowerHolderComponent.getPowerTypes(entity, IcarusWingsPowerType.class);
        if (icarusWings.isEmpty()) return original;

        IcarusWingsPowerType.Overrides overrides = icarusWings.getFirst().getOverrides();
        return new IcarusPlayerValues() {
            @Override
            public float wingsSpeed() {
                return overrides.wingsSpeed().orElseGet(original::wingsSpeed);
            }

            @Override
            public float maxSlowedMultiplier() {
                return overrides.maxSlowedMultiplier().orElseGet(original::maxSlowedMultiplier);
            }

            @Override
            public boolean armorSlows() {
                return overrides.armorSlows().orElseGet(original::armorSlows);
            }

            @Override
            public boolean canLoopDeLoop() {
                return overrides.canLoopDeLoop().orElseGet(original::canLoopDeLoop);
            }

            @Override
            public boolean canSlowFall() {
                return overrides.canSlowFall().orElseGet(original::canSlowFall);
            }

            @Override
            public float exhaustionAmount() {
                return overrides.exhaustionAmount().orElseGet(original::exhaustionAmount);
            }

            @Override
            public int maxHeightAboveWorld() {
                return overrides.maxHeightAboveWorld().orElseGet(original::maxHeightAboveWorld);
            }

            @Override
            public boolean maxHeightEnabled() {
                return overrides.maxHeightEnabled().orElseGet(original::maxHeightEnabled);
            }

            @Override
            public boolean dropOutOfSkyWhenTired() {
                return overrides.dropOutOfSkyWhenTired().orElseGet(original::dropOutOfSkyWhenTired);
            }

            @Override
            public boolean useStaminaForFlight() {
                return overrides.useStaminaForFlight().orElseGet(original::useStaminaForFlight);
            }

            @Override
            public float staminaAmount() {
                return overrides.staminaAmount().orElseGet(original::staminaAmount);
            }

            @Override
            public float staminaRegen() {
                return overrides.staminaRegen().orElseGet(original::staminaRegen);
            }

            @Override
            public float requiredFoodAmount() {
                return overrides.requiredFoodAmount().orElseGet(original::requiredFoodAmount);
            }
        };
    }

    @WrapOperation(method = "hasWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean hasWingsFromOrigin(Predicate<LivingEntity> instance, Object entity, Operation<Boolean> original) {
        return PowerHolderComponent.hasPowerType((LivingEntity) entity, IcarusWingsPowerType.class)
                || original.call(instance, entity);
    }

    @WrapOperation(method = "getEquippedWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object getOriginWings(Function<LivingEntity, ItemStack> instance, Object entity, Operation<ItemStack> original) {
        if (PowerHolderComponent.hasPowerType((LivingEntity) entity, IcarusWingsPowerType.class)) {
            return null;
        }
        return original.call(instance, entity);
    }
}