package dev.muon.medievalorigins.condition.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class CoveredByBlockCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Predicate<BlockInWorld> blockCondition = data.get("block_condition");
        
        AABB boundingBox = entity.getBoundingBox();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        BlockPos minPos = BlockPos.containing(boundingBox.minX + 0.001D, boundingBox.minY + 0.001D, boundingBox.minZ + 0.001D);
        BlockPos maxPos = BlockPos.containing(boundingBox.maxX - 0.001D, boundingBox.maxY - 0.001D, boundingBox.maxZ - 0.001D);

        // Check every block position that intersects with the entity's bounding box
        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    mutablePos.set(x, y, z);
                    BlockInWorld cachedBlock = new BlockInWorld(entity.level(), mutablePos, true);
                    
                    // If any block position doesn't match the condition, the entity isn't fully covered
                    if (blockCondition == null || !blockCondition.test(cachedBlock)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                MedievalOrigins.loc("covered_by_block"),
                new SerializableData()
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION),
                CoveredByBlockCondition::condition
        );
    }
}

