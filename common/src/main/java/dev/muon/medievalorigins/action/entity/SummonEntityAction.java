package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ISummon;
import dev.muon.medievalorigins.entity.SummonTracker;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class SummonEntityAction {

    private static final int MAX_SUMMONS = 5;

    public static void action(SerializableData.Instance data, Entity caster) {

        if (!caster.level().isClientSide() && caster instanceof LivingEntity livingCaster) {
            ServerLevel serverWorld = (ServerLevel) caster.level();

            Integer duration = data.get("duration");
            EntityType<?> entityType = data.get("entity_type");
            CompoundTag entityNbt = data.get("tag");
            ItemStack weapon = data.get("weapon");

            Optional<Entity> entityToSpawnOpt = MiscUtil.getEntityWithPassengers(
                    serverWorld,
                    entityType,
                    entityNbt,
                    caster.position(),
                    caster.getYRot(),
                    caster.getXRot()
            );

            if (entityToSpawnOpt.isEmpty()) {
                return;
            }

            Entity entityToSpawn = entityToSpawnOpt.get();

            if (entityToSpawn instanceof Mob mob) {
                DifficultyInstance difficulty = serverWorld.getCurrentDifficultyAt(mob.blockPosition());
                mob.finalizeSpawn(serverWorld, difficulty, MobSpawnType.MOB_SUMMONED, null, entityNbt);
                mob.setPersistenceRequired();
            }

            if (entityToSpawn instanceof ISummon summon) {
                if (duration != null) {
                    summon.setLifeTicks(duration);
                    summon.setIsLimitedLife(true);
                } else {
                    summon.setIsLimitedLife(false);
                }
                summon.setOwner(livingCaster);
                summon.setOwnerID(caster.getUUID());

                if (weapon != null && !weapon.isEmpty()) {
                    summon.setWeapon(weapon);
                }
            }

            serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);

            // Manage summon limit
            manageSummonLimit(caster);

            data.<Consumer<Entity>>ifPresent("entity_action", entityAction -> entityAction.accept(entityToSpawn));
            data.<Consumer<Tuple<Entity, Entity>>>ifPresent("bientity_action", biEntityAction -> biEntityAction.accept(new Tuple<>(caster, entityToSpawn)));
        }
    }

    private static void manageSummonLimit(Entity owner) {
        Collection<ISummon> existingSummons = SummonTracker.getSummonsForOwner(owner.getUUID());
        if (existingSummons.size() >= MAX_SUMMONS) {
            List<ISummon> summonsList = existingSummons.stream()
                    .sorted(createSummonComparator())
                    .collect(Collectors.toList());

            ISummon toRemove = summonsList.get(0);
            Mob mobToRemove = toRemove.getSelfAsMob();

            if (owner instanceof Player player) {
                int x = (int) Math.round(mobToRemove.getX());
                int y = (int) Math.round(mobToRemove.getY());
                int z = (int) Math.round(mobToRemove.getZ());

                String dimension = mobToRemove.level().dimension().location().getPath();

                Component message = Component.translatable("message.medievalorigins.summon_limit_reached")
                        .append(" ")
                        .append(mobToRemove.getDisplayName())
                        .append(" ")
                        .append(Component.translatable("message.medievalorigins.summon_location",
                                dimension, x, y, z));

                player.displayClientMessage(message, true);
            }

            mobToRemove.remove(Entity.RemovalReason.DISCARDED);
            SummonTracker.untrackSummon(toRemove);
        }
    }

    private static Comparator<ISummon> createSummonComparator() {
        return (a, b) -> {
            LivingEntity entityA = a.getLivingEntity();
            LivingEntity entityB = b.getLivingEntity();
            
            if (entityA == null || entityB == null) {
                return entityA == null ? -1 : 1;
            }

            // Compare by limited life status (limited life mobs are removed first)
            boolean aLimited = a.isLimitedLife();
            boolean bLimited = b.isLimitedLife();
            
            if (aLimited != bLimited) {
                return aLimited ? -1 : 1;
            }

            // If both are limited life, compare remaining ticks
            if (aLimited && bLimited) {
                return Integer.compare(a.getTicksLeft(), b.getTicksLeft());
            }

            // Compare by type priority
            return compareMobTypes(toMob(entityA), toMob(entityB));
        };
    }

    private static Mob toMob(LivingEntity entity) {
        return entity instanceof Mob ? (Mob) entity : null;
    }

    private static int compareMobTypes(Mob a, Mob b) {
        if (a == null || b == null) {
            return a == null ? -1 : 1;
        }
        int priorityA = getMobPriority(a);
        int priorityB = getMobPriority(b);
        return Integer.compare(priorityA, priorityB);
    }

    private static int getMobPriority(Mob mob) {
        String entityId = mob.getType().toString();
        // Higher number = higher priority (kept longer)
        return switch (entityId) {
            case "entity.medievalorigins.summon_wither_skeleton" -> 3;
            case "entity.medievalorigins.summon_skeleton" -> 2;
            case "entity.medievalorigins.summon_zombie" -> 1;
            default -> 0;
        };
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