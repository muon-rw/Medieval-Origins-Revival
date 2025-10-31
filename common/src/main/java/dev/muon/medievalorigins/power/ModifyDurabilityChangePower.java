package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ModifyDurabilityChangePower extends ValueModifyingPower {
    private final Predicate<ItemStack> itemCondition;
    private final Comparison comparison;
    private final int compareTo;
    private final Function function;

    public ModifyDurabilityChangePower(PowerType<?> type, LivingEntity entity,
                                       Predicate<ItemStack> itemCondition,
                                       Comparison comparison,
                                       int compareTo,
                                       Function function) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.comparison = comparison;
        this.compareTo = compareTo;
        this.function = function;
    }

    public boolean doesApply(Level level, ItemStack stack, int durabilityChange) {
        return (itemCondition == null || itemCondition.test(stack)) 
                && comparison.compare(durabilityChange, compareTo);
    }

    public int postFunction(double value) {
        int retVal = switch (function) {
            case CEILING -> Mth.abs(Mth.ceil(value));
            case ROUND -> (int) Mth.abs(Math.round(value));
            default -> Mth.abs(Mth.floor(value));
        };

        if (value < 0) {
            retVal = -retVal;
        }

        return retVal;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("modify_durability_change"),
                new SerializableData()
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                        .add("compare_to", SerializableDataTypes.INT, Integer.MIN_VALUE)
                        .add("function", SerializableDataType.enumValue(Function.class), Function.FLOOR)
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null),
                data -> (type, player) -> {
                    ModifyDurabilityChangePower power = new ModifyDurabilityChangePower(
                            type,
                            player,
                            data.get("item_condition"),
                            data.get("comparison"),
                            data.getInt("compare_to"),
                            data.get("function")
                    );
                    Objects.requireNonNull(power);
                    data.ifPresent("modifier", power::addModifier);
                    data.ifPresent("modifiers", (List<Modifier> mods) -> {
                        Objects.requireNonNull(power);
                        mods.forEach(power::addModifier);
                    });
                    return power;
                }
        ).allowCondition();
    }

    public enum Function {
        FLOOR,
        ROUND,
        CEILING
    }
}
