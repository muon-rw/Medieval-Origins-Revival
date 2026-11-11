package dev.muon.medievalorigins.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.data.ParticleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class AreaBonemealAction implements ActionType<EntityCtx, AreaBonemealAction.Cfg> {

    public record Cfg(int radius, int verticalRadius, Optional<ParticleEffect> particleEffect, int blocksAffectedPerTick) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("radius", 5).forGetter(Cfg::radius),
            Codec.INT.optionalFieldOf("vertical_radius", 2).forGetter(Cfg::verticalRadius),
            ParticleEffect.CODEC.optionalFieldOf("particle_effect").forGetter(Cfg::particleEffect),
            Codec.INT.optionalFieldOf("blocks_affected_per_tick", 10).forGetter(Cfg::blocksAffectedPerTick)
    ).apply(i, Cfg::new));

    @Override
    public MapCodec<Cfg> codec() {
        return CODEC;
    }

    @Override
    public void run(Cfg cfg, EntityCtx ctx) {
        Entity entity = ctx.entity();
        Level level = entity.level();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ParticleOptions particle = cfg.particleEffect().map(effect -> effect.resolve(level, entity)).orElse(null);
        int radius = cfg.radius();
        int verticalRadius = cfg.verticalRadius();

        BlockPos center = entity.blockPosition();
        int bonemealedCount = 0;
        int maxToBonemeal = 30;

        for (int y = -verticalRadius; y <= verticalRadius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (bonemealedCount >= maxToBonemeal) break;
                    if (x * x + z * z > radius * radius) continue;

                    BlockPos currentPos = center.offset(x, y, z);
                    BlockState blockState = level.getBlockState(currentPos);

                    if (blockState.getBlock() instanceof BonemealableBlock bonemealable
                            && bonemealable.isValidBonemealTarget(level, currentPos, blockState)
                            && bonemealable.isBonemealSuccess(level, serverLevel.random, currentPos, blockState)) {
                        bonemealable.performBonemeal(serverLevel, serverLevel.random, currentPos, blockState);
                        if (particle != null) {
                            serverLevel.sendParticles(particle, currentPos.getX() + 0.5, currentPos.getY() + 0.5, currentPos.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.0);
                        } else {
                            serverLevel.levelEvent(1505, currentPos, 0);
                        }
                        bonemealedCount++;
                    }
                }
                if (bonemealedCount >= maxToBonemeal) break;
            }
            if (bonemealedCount >= maxToBonemeal) break;
        }
    }
}
