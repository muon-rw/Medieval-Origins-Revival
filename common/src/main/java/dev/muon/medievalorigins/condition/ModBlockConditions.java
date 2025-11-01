package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.condition.block.IsCropCondition;
import dev.muon.medievalorigins.condition.block.IsFlowerCondition;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class ModBlockConditions {
    public static void register() {
        register(IsCropCondition.getFactory());
        register(IsFlowerCondition.getFactory());
    }

    private static void register(ConditionFactory<BlockInWorld> conditionFactory) {
        Registry.register(ApoliRegistries.BLOCK_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}

