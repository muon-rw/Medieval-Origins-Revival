package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ISummon;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;


public class SummonEntityAction {

    public static void action(SerializableData.Instance data, Entity caster) {

        if (!caster.level().isClientSide() && caster instanceof LivingEntity livingCaster) {
            ServerLevel serverWorld = (ServerLevel) caster.level();

            Optional<Integer> duration = data.get("duration");
            EntityType<?> entityType = data.get("entity_type");
            CompoundTag entityNbt = data.get("tag");
            ItemStack weapon = data.get("weapon");

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

            if (entityToSpawn instanceof ISummon summon) {
                if (duration != null) {
                    summon.setLifeTicks(duration.get());
                    summon.setIsLimitedLife(true);
                } else {
                    summon.setIsLimitedLife(false);
                }
                summon.setOwner(livingCaster);
                summon.setOwnerID(caster.getUUID());

                serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
                if (weapon != null) {
                    summon.setWeapon(data.get("weapon"));
                }
                data.<Consumer<Entity>>ifPresent("entity_action", entityAction -> entityAction.accept(entityToSpawn));
                data.<Consumer<Tuple<Entity, Entity>>>ifPresent("bientity_action", biEntityAction -> biEntityAction.accept(new Tuple<>(caster, entityToSpawn)));

                // Just in case anything unexpected changes the equipped item
                //summon.reassessWeaponGoal();
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("summon_entity"),
                new SerializableData()
                        .add("entity_type", SerializableDataTypes.ENTITY_TYPE)
                        .add("weapon", SerializableDataTypes.ITEM_STACK, null)
                        .add("duration", SerializableDataTypes.INT, null)
                        .add("tag", SerializableDataTypes.NBT, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null),
                SummonEntityAction::action
        );
    }

}