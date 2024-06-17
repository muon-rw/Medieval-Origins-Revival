package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.Constants;
import dev.muon.medievalorigins.entity.ISummon;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import dev.muon.medievalorigins.configuration.SummonEntityConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Optional;
import java.util.function.Consumer;


public class SummonEntityAction extends EntityAction<SummonEntityConfiguration> {

    public SummonEntityAction() {
        super(SummonEntityConfiguration.CODEC);
    }
    @Override
    public void execute(SummonEntityConfiguration configuration, Entity caster) {
        if (caster.level().isClientSide())
            return;
        ServerLevel serverWorld = (ServerLevel) caster.level();

        Optional<Entity> opt$entityToSpawn = MiscUtil.getEntityWithPassengers(
                serverWorld,
                configuration.entityType(),
                configuration.tag(),
                caster.position(),
                caster.getYRot(),
                caster.getXRot()
        );

        if (opt$entityToSpawn.isEmpty()) return;
        Entity entityToSpawn = opt$entityToSpawn.get();

        if (entityToSpawn instanceof Mob mob) {
            DifficultyInstance difficulty = serverWorld.getCurrentDifficultyAt(mob.blockPosition());
            MobSpawnType spawnType = MobSpawnType.MOB_SUMMONED;
            // but why
            ForgeEventFactory.onFinalizeSpawn(mob, serverWorld, difficulty, spawnType, null, configuration.tag());
            // mob.finalizeSpawn(serverWorld, difficulty, spawnType, null, configuration.tag());
            mob.setPersistenceRequired();
        }

        serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
        ConfiguredEntityAction.execute(configuration.action(), entityToSpawn);

        if (entityToSpawn instanceof ISummon summon) {
;
            if (caster instanceof LivingEntity livingCaster) {
                configuration.duration().ifPresent(duration -> {
                    summon.setLifeTicks(duration);
                    summon.setIsLimitedLife(true);
                });

                if (configuration.duration().isEmpty()) {
                    summon.setIsLimitedLife(false);
                }

                summon.setOwner(livingCaster);
                summon.setOwnerID(livingCaster.getUUID());
            }
        }

        ConfiguredBiEntityAction.execute(configuration.biEntityAction(), caster, entityToSpawn);

        configuration.weapon().ifPresent(weapon -> {
            if (entityToSpawn instanceof ISummon summon) {
                summon.setWeapon(weapon);
            }
        });
    }
}
