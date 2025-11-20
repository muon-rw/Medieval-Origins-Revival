package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.action.ModEntityActionTypes;
import dev.muon.medievalorigins.entity.SummonTracker;
import dev.muon.medievalorigins.entity.SummonedMob;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.BiEntityAction;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SummonEntityActionType extends EntityActionType {

    private static final int MAX_SUMMONS = 5;

    public static final TypedDataObjectFactory<SummonEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("entity_type", SerializableDataTypes.ENTITY_TYPE)
                    .add("weapon", SerializableDataTypes.ITEM_STACK, null)
                    .add("duration", SerializableDataTypes.INT.optional(), Optional.empty())
                    .add("tag", SerializableDataTypes.NBT_COMPOUND, new CompoundTag())
                    .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
                    .add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty()),
            data -> new SummonEntityActionType(
                    data.get("entity_type"),
                    data.get("weapon"),
                    data.get("duration"),
                    data.get("tag"),
                    data.get("entity_action"),
                    data.get("bientity_action")
            ),
            (type, data) -> data.instance()
                    .set("entity_type", type.entityType)
                    .set("weapon", type.weapon)
                    .set("duration", type.duration)
                    .set("tag", type.entityNbt)
                    .set("entity_action", type.entityAction)
                    .set("bientity_action", type.biEntityAction)
    );

    private final EntityType<?> entityType;
    private final ItemStack weapon;
    private final Optional<Integer> duration;
    private final CompoundTag entityNbt;
    private final Optional<EntityAction> entityAction;
    private final Optional<BiEntityAction> biEntityAction;

    public SummonEntityActionType(EntityType<?> entityType, ItemStack weapon, Optional<Integer> duration,
                                  CompoundTag entityNbt, Optional<EntityAction> entityAction,
                                  Optional<BiEntityAction> biEntityAction) {
        this.entityType = entityType;
        this.weapon = weapon;
        this.duration = duration;
        this.entityNbt = entityNbt;
        this.entityAction = entityAction;
        this.biEntityAction = biEntityAction;
    }

    @Override
    public void accept(EntityActionContext context) {
        Entity entity = context.entity();
        if (!(entity.level() instanceof ServerLevel serverWorld) || !(entity instanceof LivingEntity livingCaster)) {
            return;
        }

        Optional<Entity> entityToSpawn = MiscUtil.getEntityWithPassengers(
                serverWorld,
                entityType,
                entityNbt,
                entity.position(),
                entity.getYRot(),
                entity.getXRot()
        );

        if (entityToSpawn.isEmpty()) {
            return;
        }

        Entity actualEntityToSpawn = entityToSpawn.get();

        if (actualEntityToSpawn instanceof Mob mob) {
            DifficultyInstance difficulty = serverWorld.getCurrentDifficultyAt(mob.blockPosition());
            mob.finalizeSpawn(serverWorld, difficulty, MobSpawnType.MOB_SUMMONED, null);
            mob.setPersistenceRequired();
        }

        if (actualEntityToSpawn instanceof SummonedMob summon) {
            duration.ifPresentOrElse(
                    ticks -> {
                        summon.setLifeTicks(ticks);
                        summon.setIsLimitedLife(true);
                    },
                    () -> summon.setIsLimitedLife(false)
            );

            summon.setOwner(livingCaster);
            summon.setOwnerID(entity.getUUID());

            if (weapon != null && !weapon.isEmpty()) {
                summon.setWeapon(weapon);
            }
        }

        serverWorld.tryAddFreshEntityWithPassengers(actualEntityToSpawn);

        manageSummonLimit(entity);

        entityAction.ifPresent(action -> action.execute(actualEntityToSpawn));
        biEntityAction.ifPresent(action -> action.execute(entity, actualEntityToSpawn));
    }

    private void manageSummonLimit(Entity owner) {
        Collection<SummonedMob> existingSummons = SummonTracker.getSummonsForOwner(owner.getUUID());
        if (existingSummons.size() > MAX_SUMMONS) {
            List<SummonedMob> summonsList = existingSummons.stream()
                    .sorted(createSummonComparator())
                    .toList();

            SummonedMob toRemove = summonsList.get(0);
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

            toRemove.getSelfAsMob().remove(Entity.RemovalReason.DISCARDED);
            toRemove.getSelfAsMob().gameEvent(GameEvent.ENTITY_DIE);
            SummonTracker.untrackSummon(toRemove);
        }
    }

    private Comparator<SummonedMob> createSummonComparator() {
        return (a, b) -> {
            // First, compare by limited life status
            if (a.isLimitedLife() != b.isLimitedLife()) {
                return a.isLimitedLife() ? -1 : 1; // Limited life mobs are removed first
            }

            // If both are limited life, compare remaining ticks
            if (a.isLimitedLife() && b.isLimitedLife()) {
                return Integer.compare(a.getLifeTicks(), b.getLifeTicks());
            }

            // If we get here, either both are infinite or have same life ticks
            // Compare by type priority
            return compareMobTypes(a.getSelfAsMob(), b.getSelfAsMob());
        };
    }

    private int compareMobTypes(Mob a, Mob b) {
        int priorityA = getMobPriority(a);
        int priorityB = getMobPriority(b);
        return Integer.compare(priorityA, priorityB);
    }

    private int getMobPriority(Mob mob) {
        String entityId = mob.getType().toString();
        // Higher number = higher priority (kept longer)
        return switch (entityId) {
            case "entity.medievalorigins.summon_wither_skeleton" -> 3;
            case "entity.medievalorigins.summon_skeleton" -> 2;
            case "entity.medievalorigins.summon_zombie" -> 1;
            default -> 0;
        };
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModEntityActionTypes.SUMMON_ENTITY;
    }

}