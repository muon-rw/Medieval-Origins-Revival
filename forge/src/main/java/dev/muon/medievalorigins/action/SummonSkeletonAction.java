package dev.muon.medievalorigins.action;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import dev.muon.medievalorigins.configuration.FixedSummonTypeConfiguration;
import dev.muon.medievalorigins.entity.SummonedSkeleton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;


public class SummonSkeletonAction extends EntityAction<FixedSummonTypeConfiguration> {

    public SummonSkeletonAction() {
        super(FixedSummonTypeConfiguration.CODEC);
    }
    @Override
    public void execute(FixedSummonTypeConfiguration configuration, Entity caster) {
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
    }
}
