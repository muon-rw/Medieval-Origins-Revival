
package dev.muon.medievalorigins.entity;

import dev.muon.medievalorigins.entity.goal.FollowSummonerGoal;
import dev.muon.medievalorigins.entity.goal.SummonedMobGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SummonedZombie extends Zombie implements SummonedMob {
    public SummonedZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    private static final EntityDataAccessor<Boolean> IS_LIMITED_LIFE =
            SynchedEntityData.defineId(SummonedZombie.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIFE_TICKS =
            SynchedEntityData.defineId(SummonedZombie.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_LIFE_TICKS =
            SynchedEntityData.defineId(SummonedZombie.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(SummonedZombie.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(SummonedZombie.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_LIMITED_LIFE, false);
        builder.define(LIFE_TICKS, 0);
        builder.define(MAX_LIFE_TICKS, 0);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(SITTING, false);
    }


    @Override
    public void setLifeTicks(int ticks) {
        this.entityData.set(LIFE_TICKS, ticks);
        if (this.entityData.get(MAX_LIFE_TICKS) <= this.entityData.get(LIFE_TICKS)) {
            this.entityData.set(MAX_LIFE_TICKS, ticks);
        }
    }

    @Override
    public int getLifeTicks() {
        return this.entityData.get(LIFE_TICKS);
    }

    @Override
    public boolean isLimitedLife() {
        return this.entityData.get(IS_LIMITED_LIFE);
    }

    @Override
    public int getMaxLifeTicks() {
        return this.entityData.get(IS_LIMITED_LIFE) ? this.entityData.get(MAX_LIFE_TICKS): 0;
    }

    @Override
    public void setIsLimitedLife(boolean limited) {
        this.entityData.set(IS_LIMITED_LIFE, limited);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.SUMMON_ZOMBIE;
    }

    @Override
    protected void dropEquipment() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack);
            }
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isOrderedToSit() {
        return this.entityData.get(SITTING);
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
        this.entityData.set(SITTING, sitting);
    }

    @Override
    protected void registerGoals() {
        // Combat goals (highest priority)
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.1f, true));

        // Movement/behavior goals
        this.goalSelector.addGoal(1, new FollowSummonerGoal(this, 1.0, 9.0f, 3.0f));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !((SummonedMob)mob).isOrderedToSit() && super.canUse();
            }
        });

        // Looking goals (lowest priority)
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 3.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        // Targeting goals
        this.targetSelector.addGoal(1, new SummonedMobGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, true,
                (entity) -> {
                    if (entity == null || entity.getKillCredit() == null || ((SummonedMob)this).isOrderedToSit())
                        return false;
                    LivingEntity owner = getOwner();
                    return owner != null && entity.getKillCredit().equals(owner);
                }
        ));

    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isLimitedLife() && !this.level().isClientSide()) {
            int currentTicks = this.getLifeTicks();
            if (currentTicks <= 0) {
                this.hurt(this.damageSources().starve(), 20.0F);
            } else {
                this.setLifeTicks(currentTicks - 1);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (isAlliedTo(target)) {
            return false;
        }
        setKillCredit(target);
        return super.doHurtTarget(target);
    }

    @Override
    public PlayerTeam getTeam() {
        LivingEntity owner = this.getOwner();
        return owner != null ? owner.getTeam() : super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return SummonedMob.checkAllyStatus(this, entity);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("isLimited", this.isLimitedLife());
        compound.putInt("LifeTicks", this.getLifeTicks());
        compound.putInt("MaxLifeTicks", this.getMaxLifeTicks());
        UUID ownerUuid = this.getOwnerUUID();
        if (ownerUuid != null) {
            compound.putUUID("OwnerUUID", ownerUuid);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setIsLimitedLife(compound.getBoolean("isLimited"));
        this.setLifeTicks(compound.getInt("LifeTicks"));
        if (compound.contains("MaxLifeTicks")) {
            this.entityData.set(MAX_LIFE_TICKS, compound.getInt("MaxLifeTicks"));
        }
        if (compound.hasUUID("OwnerUUID")) {
            this.setOwnerID(compound.getUUID("OwnerUUID"));
        }
    }


    @Override
    public void setWeapon(ItemStack item) {
        this.setItemSlot(EquipmentSlot.MAINHAND, item);
        this.reassessWeaponGoal();
    }

    @Override
    public void reassessWeaponGoal() {
        // Zombies only melee
    }

    @Override
    public Level getWorld() {
        return this.level();
    }

    @Override
    public Mob getSelfAsMob() {
        return this;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Override
    public void setOwnerID(UUID uuid) {
        UUID oldOwner = getOwnerUUID();
        if (oldOwner != null) {
            SummonTracker.untrackSummon(this);
        }
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
        if (uuid != null) {
            SummonTracker.trackSummon(this);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        SummonTracker.untrackSummon(this);
        super.remove(reason);
    }

    // Additional overrides
    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull RandomSource randomSource, @NotNull DifficultyInstance difficulty) {
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, DifficultyInstance difficulty,
                                        @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.handleAttributes(difficulty.getSpecialMultiplier());
        return spawnGroupData;
    }
}