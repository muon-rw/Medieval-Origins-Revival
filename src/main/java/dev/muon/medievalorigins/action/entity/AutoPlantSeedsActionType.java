package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.action.ModEntityActionTypes;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AutoPlantSeedsActionType extends EntityActionType {

    private final float chance;
    private final TagKey<Block> blockTagKey;
    private final int horizontalRadius;
    private final int verticalRadiusPlayer;
    private final Random random = new Random();

    public AutoPlantSeedsActionType(SerializableData.Instance data) {
        this.chance = data.getFloat("chance");
        ResourceLocation tagId = data.getId("block_tag");
        this.blockTagKey = TagKey.create(Registries.BLOCK, tagId);
        this.horizontalRadius = data.getInt("horizontal_radius");
        this.verticalRadiusPlayer = data.getInt("vertical_radius_player");
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();
        Level level = entity.level();

        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.random.nextFloat() >= this.chance) {
            return;
        }

        BlockPos entityBasePos = entity.blockPosition();
        Registry<Block> blockRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BLOCK);
        Optional<HolderSet.Named<Block>> optionalHolderSet = blockRegistry.getTag(this.blockTagKey);

        if (optionalHolderSet.isEmpty() || optionalHolderSet.get().size() == 0) {
            return;
        }

        List<Block> possibleBlocks = optionalHolderSet.get().stream()
                .map(Holder::value)
                .toList();

        if (possibleBlocks.isEmpty()) {
            return;
        }

        List<BlockPos> potentialPlantingSpots = new ArrayList<>();
        for (int yOff = -this.verticalRadiusPlayer; yOff <= this.verticalRadiusPlayer; yOff++) {
            for (int xOff = -this.horizontalRadius; xOff <= this.horizontalRadius; xOff++) {
                for (int zOff = -this.horizontalRadius; zOff <= this.horizontalRadius; zOff++) {
                    BlockPos candidatePlantPos = entityBasePos.offset(xOff, yOff, zOff);
                    BlockPos candidateFarmlandPos = candidatePlantPos.below();

                    if (level.getBlockState(candidatePlantPos).isAir() &&
                        level.getBlockState(candidateFarmlandPos).getBlock() instanceof FarmBlock) {
                        potentialPlantingSpots.add(candidatePlantPos);
                    }
                }
            }
        }

        if (potentialPlantingSpots.isEmpty()) {
            return;
        }

        Collections.shuffle(potentialPlantingSpots, this.random);

        for (BlockPos plantPos : potentialPlantingSpots) {
            if (level.getBlockState(plantPos).isAir() &&
                level.getBlockState(plantPos.below()).getBlock() instanceof FarmBlock) {

                Block selectedBlock = possibleBlocks.get(this.random.nextInt(possibleBlocks.size()));
                BlockState plantState = selectedBlock.defaultBlockState();

                if (plantState.canSurvive(serverLevel, plantPos)) {
                    if (serverLevel.setBlock(plantPos, plantState, 3)) {
                        serverLevel.playSound(null, plantPos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                        serverLevel.gameEvent(entity, GameEvent.BLOCK_PLACE, plantPos);
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, plantPos.getX() + 0.5, plantPos.getY() + 0.5, plantPos.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.0D);
                        return; 
                    }
                }
            }
        }
    }

    public static final TypedDataObjectFactory<AutoPlantSeedsActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("chance", SerializableDataTypes.FLOAT, 0.25f)
                    .add("block_tag", SerializableDataTypes.IDENTIFIER, MedievalOrigins.loc("crops"))
                    .add("horizontal_radius", SerializableDataTypes.INT, 1)
                    .add("vertical_radius_player", SerializableDataTypes.INT, 0),
            AutoPlantSeedsActionType::new,
            (type, data) -> {
                SerializableData.Instance inst = data.new Instance();
                inst.set("chance", type.chance);
                inst.set("block_tag", type.blockTagKey.location());
                inst.set("horizontal_radius", type.horizontalRadius);
                inst.set("vertical_radius_player", type.verticalRadiusPlayer);
                return inst;
            }
    );

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModEntityActionTypes.AUTO_PLANT_SEEDS;
    }
} 