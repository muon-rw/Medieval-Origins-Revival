package dev.muon.medievalorigins.condition.block;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.condition.ConditionType;
import dev.overgrown.apoli.condition.context.BlockCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.level.block.FlowerBlock;

public final class IsFlowerCondition implements ConditionType<BlockCtx, EmptyCfg> {
    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public boolean test(EmptyCfg cfg, BlockCtx ctx) {
        return ctx.state().getBlock() instanceof FlowerBlock;
    }
}