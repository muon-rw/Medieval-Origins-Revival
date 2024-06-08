package dev.muon.medievalorigins.action;

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

import java.util.Optional;
import java.util.function.Consumer;


public class SummonEntityAction extends EntityAction<SummonEntityConfiguration> {

    public SummonEntityAction() {
        super(SummonEntityConfiguration.CODEC);
    }
    @Override
    public void execute(SummonEntityConfiguration configuration, Entity caster) {
        if (!caster.level().isClientSide() && caster instanceof LivingEntity livingCaster) {
            ServerLevel serverWorld = (ServerLevel) caster.level();

            EntityType<?> entityType = configuration.entityType();
            CompoundTag entityNbt = configuration.tag();
            ItemStack weapon = configuration.weapon().get();

            Entity entityToSpawn = MiscUtil.getEntityWithPassengers(
                    serverWorld,
                    entityType,
                    entityNbt,
                    caster.position(),
                    caster.getYRot(),
                    caster.getXRot()
            ).get();

            if (entityToSpawn instanceof Mob mob) {
                DifficultyInstance difficulty = serverWorld.getCurrentDifficultyAt(mob.blockPosition());
                MobSpawnType spawnType = MobSpawnType.MOB_SUMMONED;
                mob.finalizeSpawn(serverWorld, difficulty, spawnType, null, entityNbt);
                mob.setPersistenceRequired();
            }

            Integer duration = configuration.duration().get();

            if (entityToSpawn instanceof ISummon summon) {
                if (duration != null) {
                    summon.setLifeTicks(duration);
                    summon.setIsLimitedLife(true);
                } else {
                    summon.setIsLimitedLife(false);
                }
                summon.setOwner(livingCaster);
                summon.setOwnerID(caster.getUUID());

                serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
                if (weapon != null) {
                    summon.setWeapon(weapon);
                }

                ConfiguredEntityAction.execute(configuration.action(), entityToSpawn);
                ConfiguredBiEntityAction.execute(configuration.biEntityAction(), caster, entityToSpawn);

                // Just in case anything unexpected changes the equipped item
                //summon.reassessWeaponGoal();
            }
        }

        /**
         * break - old shit
         */

        /*
        if (!caster.level().isClientSide()) {
            ServerLevel serverWorld = (ServerLevel)caster.level();
            ItemStack weapon;

            if (configuration.weapon().isPresent()) {
                ResourceLocation weaponResource = configuration.weapon().get();
                weapon = BuiltInRegistries.ITEM.getOptional(weaponResource)
                        .map(Item::getDefaultInstance)
                        .orElse(new ItemStack(Items.BOW));
            } else {
                weapon = new ItemStack(Items.BOW);
            }

            SummonedSkeleton summon = new SummonedSkeleton(serverWorld, ((LivingEntity) caster), weapon);

            if (configuration.duration().isPresent()) {
                summon.setLimitedLife(configuration.duration().get());
            } else {
                summon.setIsLimitedLife(false);
            }
            if (configuration.tag() != null) {
                CompoundTag tag = summon.saveWithoutId(new CompoundTag());
                tag.merge(configuration.tag());
                summon.load(tag);
            }
            serverWorld.tryAddFreshEntityWithPassengers(summon);
            summon.moveTo(caster.position());
            ConfiguredEntityAction.execute(configuration.action(), summon);
        }
         */
    }
}
