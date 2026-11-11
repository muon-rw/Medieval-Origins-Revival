package dev.muon.medievalorigins.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.entity.SummonTracker;
import dev.muon.medievalorigins.entity.SummonedMob;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.action.BiEntityAction;
import dev.overgrown.apoli.action.EntityAction;
import dev.overgrown.apoli.codec.LoggedOptionalField;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.data.ItemStackData;
import dev.overgrown.apoli.data.Nbt;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class SummonEntityAction implements ActionType<EntityCtx, SummonEntityAction.Cfg> {

    private static final int MAX_SUMMONS = 5;

    public record Cfg(
            EntityType<?> entityType,
            Optional<ItemStackData> weapon,
            Optional<Integer> duration,
            Optional<Nbt> tag,
            Optional<EntityAction> entityAction,
            Optional<BiEntityAction> biEntityAction
    ) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(Cfg::entityType),
            ItemStackData.CODEC.optionalFieldOf("weapon").forGetter(Cfg::weapon),
            Codec.INT.optionalFieldOf("duration").forGetter(Cfg::duration),
            Nbt.CODEC.optionalFieldOf("tag").forGetter(Cfg::tag),
            LoggedOptionalField.of("entity_action", EntityAction.CODEC).forGetter(Cfg::entityAction),
            LoggedOptionalField.of("bientity_action", BiEntityAction.CODEC).forGetter(Cfg::biEntityAction)
    ).apply(i, Cfg::new));

    @Override
    public MapCodec<Cfg> codec() {
        return CODEC;
    }

    @Override
    public void run(Cfg cfg, EntityCtx ctx) {
        Entity entity = ctx.entity();
        if (!(entity.level() instanceof ServerLevel serverWorld) || !(entity instanceof LivingEntity livingCaster)) {
            return;
        }

        Entity actualEntityToSpawn = createEntityWithPassengers(
                serverWorld,
                cfg.entityType(),
                cfg.tag().map(Nbt::tag).orElseGet(CompoundTag::new),
                entity.position(),
                entity.getYRot(),
                entity.getXRot()
        );

        if (actualEntityToSpawn == null) {
            return;
        }

        if (actualEntityToSpawn instanceof Mob mob) {
            DifficultyInstance difficulty = serverWorld.getCurrentDifficultyAt(mob.blockPosition());
            mob.finalizeSpawn(serverWorld, difficulty, MobSpawnType.MOB_SUMMONED, null);
            mob.setPersistenceRequired();
        }

        if (actualEntityToSpawn instanceof SummonedMob summon) {
            cfg.duration().ifPresentOrElse(
                    ticks -> {
                        summon.setLifeTicks(ticks);
                        summon.setIsLimitedLife(true);
                    },
                    () -> summon.setIsLimitedLife(false)
            );

            summon.setOwner(livingCaster);
            summon.setOwnerID(entity.getUUID());

            cfg.weapon().map(ItemStackData::stack)
                    .filter(weapon -> !weapon.isEmpty())
                    .ifPresent(weapon -> summon.setWeapon(weapon.copy()));
        }

        serverWorld.tryAddFreshEntityWithPassengers(actualEntityToSpawn);

        manageSummonLimit(entity);

        cfg.entityAction().ifPresent(action -> action.run(EntityCtx.of(actualEntityToSpawn, serverWorld)));
        cfg.biEntityAction().ifPresent(action -> action.run(BiEntityCtx.of(entity, actualEntityToSpawn, serverWorld)));
    }

    private static Entity createEntityWithPassengers(ServerLevel level, EntityType<?> entityType, CompoundTag entityNbt,
                                                     Vec3 pos, float yaw, float pitch) {
        CompoundTag nbt = entityNbt.copy();
        nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
        return EntityType.loadEntityRecursive(nbt, level, loaded -> {
            loaded.moveTo(pos.x, pos.y, pos.z, yaw, pitch);
            return loaded;
        });
    }

    private static void manageSummonLimit(Entity owner) {
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

    private static Comparator<SummonedMob> createSummonComparator() {
        return (a, b) -> {
            // Limited life mobs are removed first
            if (a.isLimitedLife() != b.isLimitedLife()) {
                return a.isLimitedLife() ? -1 : 1;
            }

            if (a.isLimitedLife() && b.isLimitedLife()) {
                return Integer.compare(a.getLifeTicks(), b.getLifeTicks());
            }

            return Integer.compare(getMobPriority(a.getSelfAsMob()), getMobPriority(b.getSelfAsMob()));
        };
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
}
