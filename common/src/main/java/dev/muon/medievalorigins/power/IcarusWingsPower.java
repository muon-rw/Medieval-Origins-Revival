package dev.muon.medievalorigins.power;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Grants Icarus wings without an equipped wing item. Each optional field overrides the matching
 * Icarus config value for the holder; absent fields fall back to the Icarus config.
 */
public final class IcarusWingsPower extends PowerType<IcarusWingsPower.Config> {

    private static final Codec<ItemStack> WINGS_TYPE_CODEC = Codec.either(
            BuiltInRegistries.ITEM.byNameCodec().xmap(ItemStack::new, ItemStack::getItem),
            ItemStack.SINGLE_ITEM_CODEC
    ).xmap(either -> either.map(stack -> stack, stack -> stack), Either::right);

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WINGS_TYPE_CODEC.fieldOf("wings_type").forGetter(Config::wingsType),
            Codec.FLOAT.optionalFieldOf("wings_speed").forGetter(Config::wingsSpeed),
            Codec.FLOAT.optionalFieldOf("max_slowed_multiplier").forGetter(Config::maxSlowedMultiplier),
            Codec.BOOL.optionalFieldOf("armor_slows").forGetter(Config::armorSlows),
            Codec.BOOL.optionalFieldOf("can_loop_de_loop").forGetter(Config::canLoopDeLoop),
            Codec.BOOL.optionalFieldOf("can_slow_fall").forGetter(Config::canSlowFall),
            Codec.FLOAT.optionalFieldOf("exhaustion_amount").forGetter(Config::exhaustionAmount),
            Codec.INT.optionalFieldOf("max_height_above_world").forGetter(Config::maxHeightAboveWorld),
            Codec.BOOL.optionalFieldOf("max_height_enabled").forGetter(Config::maxHeightEnabled),
            Codec.BOOL.optionalFieldOf("drop_out_of_sky_when_tired").forGetter(Config::dropOutOfSkyWhenTired),
            Codec.BOOL.optionalFieldOf("use_stamina_for_flight").forGetter(Config::useStaminaForFlight),
            Codec.FLOAT.optionalFieldOf("stamina_amount").forGetter(Config::staminaAmount),
            Codec.FLOAT.optionalFieldOf("stamina_regen").forGetter(Config::staminaRegen),
            Codec.FLOAT.optionalFieldOf("required_food_amount").forGetter(Config::requiredFoodAmount)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    public static @Nullable Config get(LivingEntity entity) {
        return PowerLookup.firstActive(entity, ModPowers.ICARUS_WINGS, Config.class);
    }

    public record Config(
            ItemStack wingsType,
            Optional<Float> wingsSpeed,
            Optional<Float> maxSlowedMultiplier,
            Optional<Boolean> armorSlows,
            Optional<Boolean> canLoopDeLoop,
            Optional<Boolean> canSlowFall,
            Optional<Float> exhaustionAmount,
            Optional<Integer> maxHeightAboveWorld,
            Optional<Boolean> maxHeightEnabled,
            Optional<Boolean> dropOutOfSkyWhenTired,
            Optional<Boolean> useStaminaForFlight,
            Optional<Float> staminaAmount,
            Optional<Float> staminaRegen,
            Optional<Float> requiredFoodAmount
    ) {
    }
}
