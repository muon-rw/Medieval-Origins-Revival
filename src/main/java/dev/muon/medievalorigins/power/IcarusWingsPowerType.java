package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IcarusWingsPowerType extends PowerType {

    /**
     * Per-value overrides for IcarusPlayerValues
     * Empty field = fallback to config
     */
    public record Overrides(Optional<Float> wingsSpeed,
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
                            Optional<Float> requiredFoodAmount) {
    }

    private static final SerializableDataType<Optional<Float>> OPTIONAL_FLOAT =
            SerializableDataType.optional(SerializableDataTypes.FLOAT, false);
    private static final SerializableDataType<Optional<Integer>> OPTIONAL_INT =
            SerializableDataType.optional(SerializableDataTypes.INT, false);
    private static final SerializableDataType<Optional<Boolean>> OPTIONAL_BOOLEAN =
            SerializableDataType.optional(SerializableDataTypes.BOOLEAN, false);

    private final ItemStack wingsType;
    private final Overrides overrides;

    public IcarusWingsPowerType(ItemStack wingsType, Overrides overrides, Optional<EntityCondition> condition) {
        super(condition);
        this.wingsType = wingsType;
        this.overrides = overrides;
    }

    public ItemStack getWingsType() {
        return wingsType;
    }

    public Overrides getOverrides() {
        return overrides;
    }

    public static final PowerConfiguration<IcarusWingsPowerType> FACTORY = PowerConfiguration.conditionedOf(
            MedievalOrigins.loc("icarus_wings"),
            new SerializableData()
                    .add("wings_type", SerializableDataTypes.ITEM_STACK)
                    .add("wings_speed", OPTIONAL_FLOAT, Optional.empty())
                    .add("max_slowed_multiplier", OPTIONAL_FLOAT, Optional.empty())
                    .add("armor_slows", OPTIONAL_BOOLEAN, Optional.empty())
                    .add("can_loop_de_loop", OPTIONAL_BOOLEAN, Optional.empty())
                    .add("can_slow_fall", OPTIONAL_BOOLEAN, Optional.empty())
                    .add("exhaustion_amount", OPTIONAL_FLOAT, Optional.empty())
                    .add("max_height_above_world", OPTIONAL_INT, Optional.empty())
                    .add("max_height_enabled", OPTIONAL_BOOLEAN, Optional.empty())
                    .add("drop_out_of_sky_when_tired", OPTIONAL_BOOLEAN, Optional.empty())
                    .add("use_stamina_for_flight", OPTIONAL_BOOLEAN, Optional.empty())
                    .add("stamina_amount", OPTIONAL_FLOAT, Optional.empty())
                    .add("stamina_regen", OPTIONAL_FLOAT, Optional.empty())
                    .add("required_food_amount", OPTIONAL_FLOAT, Optional.empty()),
            (data, condition) -> new IcarusWingsPowerType(
                    data.get("wings_type"),
                    new Overrides(
                            data.get("wings_speed"),
                            data.get("max_slowed_multiplier"),
                            data.get("armor_slows"),
                            data.get("can_loop_de_loop"),
                            data.get("can_slow_fall"),
                            data.get("exhaustion_amount"),
                            data.get("max_height_above_world"),
                            data.get("max_height_enabled"),
                            data.get("drop_out_of_sky_when_tired"),
                            data.get("use_stamina_for_flight"),
                            data.get("stamina_amount"),
                            data.get("stamina_regen"),
                            data.get("required_food_amount")
                    ),
                    condition
            ),
            (type, data) -> data.instance()
                    .set("wings_type", type.getWingsType())
                    .set("wings_speed", type.getOverrides().wingsSpeed())
                    .set("max_slowed_multiplier", type.getOverrides().maxSlowedMultiplier())
                    .set("armor_slows", type.getOverrides().armorSlows())
                    .set("can_loop_de_loop", type.getOverrides().canLoopDeLoop())
                    .set("can_slow_fall", type.getOverrides().canSlowFall())
                    .set("exhaustion_amount", type.getOverrides().exhaustionAmount())
                    .set("max_height_above_world", type.getOverrides().maxHeightAboveWorld())
                    .set("max_height_enabled", type.getOverrides().maxHeightEnabled())
                    .set("drop_out_of_sky_when_tired", type.getOverrides().dropOutOfSkyWhenTired())
                    .set("use_stamina_for_flight", type.getOverrides().useStaminaForFlight())
                    .set("stamina_amount", type.getOverrides().staminaAmount())
                    .set("stamina_regen", type.getOverrides().staminaRegen())
                    .set("required_food_amount", type.getOverrides().requiredFoodAmount())
    );

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return ModPowerTypes.ICARUS_WINGS;
    }
}
