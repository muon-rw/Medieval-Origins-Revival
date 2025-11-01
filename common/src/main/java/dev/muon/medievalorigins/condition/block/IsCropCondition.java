package dev.muon.medievalorigins.condition.block;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class IsCropCondition {

    public static boolean condition(SerializableData.Instance data, BlockInWorld cachedBlock) {
        BlockState state = cachedBlock.getState();
        return state.getBlock() instanceof CropBlock;
    }

    public static ConditionFactory<BlockInWorld> getFactory() {
        return new ConditionFactory<>(
                MedievalOrigins.loc("is_crop"),
                new SerializableData(),
                IsCropCondition::condition
        );
    }
}

