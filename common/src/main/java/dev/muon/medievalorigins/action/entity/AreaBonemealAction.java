package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AreaBonemealAction {

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("area_bonemeal"),
                new SerializableData()
                        .add("radius", SerializableDataTypes.INT, 5)
                        .add("vertical_radius", SerializableDataTypes.INT, 2)
                        .add("particle_effect", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null),
                AreaBonemealAction::action
        );
    }

    public static void action(SerializableData.Instance data, Entity entity) {
        Level level = entity.level();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        int radius = data.getInt("radius");
        int verticalRadius = data.getInt("vertical_radius");
        ParticleOptions customParticleOptions = data.get("particle_effect");

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

                    if (blockState.getBlock() instanceof BonemealableBlock bonemealable) {
                        if (bonemealable.isValidBonemealTarget(level, currentPos, blockState, false)) {
                            if (bonemealable.isBonemealSuccess(level, serverLevel.random, currentPos, blockState)) {
                                bonemealable.performBonemeal(serverLevel, serverLevel.random, currentPos, blockState);
                                if (customParticleOptions != null) {
                                    serverLevel.sendParticles(customParticleOptions, currentPos.getX() + 0.5, currentPos.getY() + 0.5, currentPos.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.0);
                                } else {
                                    serverLevel.levelEvent(1505, currentPos, 0);
                                }
                                bonemealedCount++;
                            }
                        }
                    }
                }
                if (bonemealedCount >= maxToBonemeal) break;
            }
            if (bonemealedCount >= maxToBonemeal) break;
        }
    }
}

