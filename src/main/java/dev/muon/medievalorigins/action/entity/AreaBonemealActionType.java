package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.action.ModEntityActionTypes;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AreaBonemealActionType extends EntityActionType {

    private final int radius;
    private final int verticalRadius;
    private final ParticleOptions customParticleOptions; 
    private final int blocksAffectedPerTickConfig;

    public AreaBonemealActionType(SerializableData.Instance data) {
        this.radius = data.getInt("radius");
        this.verticalRadius = data.getInt("vertical_radius");
        this.blocksAffectedPerTickConfig = data.getInt("blocks_affected_per_tick");
        this.customParticleOptions = data.get("particle_effect"); 
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();
        Level level = entity.level();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

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
                        if (bonemealable.isValidBonemealTarget(level, currentPos, blockState)) {
                            if (bonemealable.isBonemealSuccess(level, serverLevel.random, currentPos, blockState)) {
                                bonemealable.performBonemeal(serverLevel, serverLevel.random, currentPos, blockState);
                                if (this.customParticleOptions != null) { 
                                    serverLevel.sendParticles(this.customParticleOptions, currentPos.getX() + 0.5, currentPos.getY() + 0.5, currentPos.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.0);
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

    public static final TypedDataObjectFactory<AreaBonemealActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("radius", SerializableDataTypes.INT, 5)
                    .add("vertical_radius", SerializableDataTypes.INT, 2)
                    .add("particle_effect", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null) 
                    .add("blocks_affected_per_tick", SerializableDataTypes.INT, 10),
            (data) -> new AreaBonemealActionType(data),
            (type, data) -> data.new Instance()
                    .set("radius", type.radius)
                    .set("vertical_radius", type.verticalRadius)
                    .set("particle_effect", type.customParticleOptions)
                    .set("blocks_affected_per_tick", type.blocksAffectedPerTickConfig)
    );

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModEntityActionTypes.AREA_BONEMEAL; 
    }
} 