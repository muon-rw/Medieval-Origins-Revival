package dev.muon.medievalorigins.condition.block;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class IsFlowerCondition {

    public static boolean condition(SerializableData.Instance data, BlockInWorld cachedBlock) {
        if (cachedBlock == null) {
            return false;
        }
        BlockState state = cachedBlock.getState();
        return state.getBlock() instanceof FlowerBlock;
    }

    public static ConditionFactory<BlockInWorld> getFactory() {
        return new ConditionFactory<>(
                MedievalOrigins.loc("is_flower"),
                new SerializableData(),
                IsFlowerCondition::condition
        );
    }
}

