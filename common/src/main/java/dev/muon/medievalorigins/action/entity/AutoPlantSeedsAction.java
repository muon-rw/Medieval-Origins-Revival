package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AutoPlantSeedsAction {

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("auto_plant_seeds"),
                new SerializableData()
                        .add("chance", SerializableDataTypes.FLOAT, 0.25f)
                        .add("block_tag", SerializableDataTypes.IDENTIFIER, MedievalOrigins.loc("crops"))
                        .add("horizontal_radius", SerializableDataTypes.INT, 1)
                        .add("vertical_radius_player", SerializableDataTypes.INT, 0),
                AutoPlantSeedsAction::action
        );
    }

    public static void action(SerializableData.Instance data, Entity entity) {
        Level level = entity.level();

        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        float chance = data.getFloat("chance");
        ResourceLocation tagId = data.getId("block_tag");
        int horizontalRadius = data.getInt("horizontal_radius");
        int verticalRadiusPlayer = data.getInt("vertical_radius_player");

        Random random = new Random();
        if (random.nextFloat() >= chance) {
            return;
        }

        TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, tagId);
        BlockPos entityBasePos = entity.blockPosition();
        Registry<Block> blockRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BLOCK);
        Optional<HolderSet.Named<Block>> optionalHolderSet = blockRegistry.getTag(blockTagKey);

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
        for (int yOff = -verticalRadiusPlayer; yOff <= verticalRadiusPlayer; yOff++) {
            for (int xOff = -horizontalRadius; xOff <= horizontalRadius; xOff++) {
                for (int zOff = -horizontalRadius; zOff <= horizontalRadius; zOff++) {
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

        Collections.shuffle(potentialPlantingSpots, random);

        for (BlockPos plantPos : potentialPlantingSpots) {
            if (level.getBlockState(plantPos).isAir() &&
                level.getBlockState(plantPos.below()).getBlock() instanceof FarmBlock) {

                Block selectedBlock = possibleBlocks.get(random.nextInt(possibleBlocks.size()));
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
}

