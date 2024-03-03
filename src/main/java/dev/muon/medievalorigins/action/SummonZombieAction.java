package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonedZombie;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;
import java.util.function.Consumer;


public class SummonZombieAction {

    public static void action(SerializableData.Instance data, Entity caster) {

        if (caster.level().isClientSide) return;

        ServerLevel serverWorld = (ServerLevel)caster.level();
        Optional<Integer> duration = data.get("duration");
        EntityType<?> entityType = ModEntities.SUMMON_ZOMBIE;
        CompoundTag entityNbt = data.get("tag");
        Entity entityToSpawn = MiscUtil.getEntityWithPassengers(
                serverWorld,
                entityType,
                entityNbt,
                caster.position(),
                caster.getYRot(),
                caster.getXRot()
        ).get();
        SummonedZombie summon = (SummonedZombie) entityToSpawn;

        if (duration != null) {
            summon.setLimitedLife(duration.get());
        } else {
            summon.setIsLimitedLife(false);
        }
        summon.setOwnerID(caster.getUUID());
        if (entityNbt != null) {
            CompoundTag tag = summon.saveWithoutId(new CompoundTag());
            tag.merge(entityNbt);
            summon.load(tag);
        }
        serverWorld.tryAddFreshEntityWithPassengers(summon);

        data.<Consumer<Entity>>ifPresent("entity_action", entityAction -> entityAction.accept(summon));
        data.<Consumer<Tuple<Entity, Entity>>>ifPresent("bientity_action", biEntityAction -> biEntityAction.accept(new Tuple<>(caster, entityToSpawn)));
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("summon_zombie"),
                new SerializableData()
                        .add("duration", SerializableDataTypes.INT, null)
                        .add("tag", SerializableDataTypes.NBT, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null),
                SummonZombieAction::action
        );
    }

}