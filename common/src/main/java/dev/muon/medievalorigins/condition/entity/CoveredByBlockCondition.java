package dev.muon.medievalorigins.condition.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.condition.BlockCondition;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.BlockCtx;
import dev.overgrown.apoli.condition.context.EntityCtx;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class CoveredByBlockCondition implements ConditionType<EntityCtx, CoveredByBlockCondition.Cfg> {
    public record Cfg(BlockCondition blockCondition) {}

    @Override
    public MapCodec<Cfg> codec() {
        return RecordCodecBuilder.mapCodec(i -> i.group(
                BlockCondition.CODEC.fieldOf("block_condition").forGetter(Cfg::blockCondition)
        ).apply(i, Cfg::new));
    }

    @Override
    public boolean test(Cfg cfg, EntityCtx ctx) {
        Entity entity = ctx.raw();
        Level level = entity.level();
        AABB boundingBox = entity.getBoundingBox();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        BlockPos minPos = BlockPos.containing(boundingBox.minX + 0.001D, boundingBox.minY + 0.001D, boundingBox.minZ + 0.001D);
        BlockPos maxPos = BlockPos.containing(boundingBox.maxX - 0.001D, boundingBox.maxY - 0.001D, boundingBox.maxZ - 0.001D);

        // Check every block position that intersects with the entity's bounding box
        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    mutablePos.set(x, y, z);
                    // If any block position doesn't match the condition, the entity isn't fully covered
                    if (!cfg.blockCondition().test(new BlockCtx(mutablePos, level.getBlockState(mutablePos), level))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}