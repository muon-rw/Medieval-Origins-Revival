package dev.muon.medievalorigins.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.codec.IdCodecs;
import dev.overgrown.apoli.condition.context.EntityCtx;
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

public final class AutoPlantSeedsAction implements ActionType<EntityCtx, AutoPlantSeedsAction.Cfg> {

    public record Cfg(float chance, ResourceLocation blockTag, int horizontalRadius, int verticalRadiusPlayer) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("chance", 0.25f).forGetter(Cfg::chance),
            IdCodecs.ID.optionalFieldOf("block_tag", MedievalOrigins.loc("crops")).forGetter(Cfg::blockTag),
            Codec.INT.optionalFieldOf("horizontal_radius", 1).forGetter(Cfg::horizontalRadius),
            Codec.INT.optionalFieldOf("vertical_radius_player", 0).forGetter(Cfg::verticalRadiusPlayer)
    ).apply(i, Cfg::new));

    private final Random random = new Random();

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

        if (this.random.nextFloat() >= cfg.chance()) {
            return;
        }

        BlockPos entityBasePos = entity.blockPosition();
        Registry<Block> blockRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BLOCK);
        Optional<HolderSet.Named<Block>> optionalHolderSet = blockRegistry.getTag(TagKey.create(Registries.BLOCK, cfg.blockTag()));

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
        for (int yOff = -cfg.verticalRadiusPlayer(); yOff <= cfg.verticalRadiusPlayer(); yOff++) {
            for (int xOff = -cfg.horizontalRadius(); xOff <= cfg.horizontalRadius(); xOff++) {
                for (int zOff = -cfg.horizontalRadius(); zOff <= cfg.horizontalRadius(); zOff++) {
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
}
