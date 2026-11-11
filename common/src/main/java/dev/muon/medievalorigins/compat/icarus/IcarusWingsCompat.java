package dev.muon.medievalorigins.compat.icarus;

import dev.cammiescorner.icarus.api.IcarusPlayerValues;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.power.IcarusWingsPower;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

public final class IcarusWingsCompat {
    private static final AtomicBoolean HOOKS_INSTALLED = new AtomicBoolean();

    private IcarusWingsCompat() {
    }

    /**
     * Wraps Icarus's wing lookups so power holders count as winged. Icarus assigns its own lookups
     * during init, so we install lazily from the first {@code getConfigValues} call instead.
     */
    public static void installHooks() {
        if (!HOOKS_INSTALLED.compareAndSet(false, true)) {
            return;
        }
        Predicate<LivingEntity> previousHasWings = IcarusHelper.hasWings;
        IcarusHelper.hasWings = entity -> IcarusWingsPower.get(entity) != null || previousHasWings.test(entity);

        // Icarus treats null as "no wing item", which skips its durability and item checks for power wings
        Function<LivingEntity, ItemStack> previousEquippedWings = IcarusHelper.getEquippedWings;
        IcarusHelper.getEquippedWings = entity -> IcarusWingsPower.get(entity) != null ? null : previousEquippedWings.apply(entity);
    }

    public static IcarusPlayerValues withOverrides(IcarusWingsPower.Config cfg, IcarusPlayerValues fallback) {
        return new IcarusPlayerValues() {
            @Override
            public float wingsSpeed() {
                return cfg.wingsSpeed().orElseGet(fallback::wingsSpeed);
            }

            @Override
            public float maxSlowedMultiplier() {
                return cfg.maxSlowedMultiplier().orElseGet(fallback::maxSlowedMultiplier);
            }

            @Override
            public boolean armorSlows() {
                return cfg.armorSlows().orElseGet(fallback::armorSlows);
            }

            @Override
            public boolean canLoopDeLoop() {
                return cfg.canLoopDeLoop().orElseGet(fallback::canLoopDeLoop);
            }

            @Override
            public boolean canSlowFall() {
                return cfg.canSlowFall().orElseGet(fallback::canSlowFall);
            }

            @Override
            public float exhaustionAmount() {
                return cfg.exhaustionAmount().orElseGet(fallback::exhaustionAmount);
            }

            @Override
            public int maxHeightAboveWorld() {
                return cfg.maxHeightAboveWorld().orElseGet(fallback::maxHeightAboveWorld);
            }

            @Override
            public boolean maxHeightEnabled() {
                return cfg.maxHeightEnabled().orElseGet(fallback::maxHeightEnabled);
            }

            @Override
            public boolean dropOutOfSkyWhenTired() {
                return cfg.dropOutOfSkyWhenTired().orElseGet(fallback::dropOutOfSkyWhenTired);
            }

            @Override
            public boolean useStaminaForFlight() {
                return cfg.useStaminaForFlight().orElseGet(fallback::useStaminaForFlight);
            }

            @Override
            public float staminaAmount() {
                return cfg.staminaAmount().orElseGet(fallback::staminaAmount);
            }

            @Override
            public float staminaRegen() {
                return cfg.staminaRegen().orElseGet(fallback::staminaRegen);
            }

            @Override
            public TagKey<LevelStem> noFlyingAllowedInLevels() {
                return fallback.noFlyingAllowedInLevels();
            }

            @Override
            public TagKey<DimensionType> noFlyingAllowedInDimensions() {
                return fallback.noFlyingAllowedInDimensions();
            }

            @Override
            public float requiredFoodAmount() {
                return cfg.requiredFoodAmount().orElseGet(fallback::requiredFoodAmount);
            }
        };
    }
}
