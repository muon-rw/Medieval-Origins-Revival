package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.configuration.FixedSummonTypeConfiguration;
import dev.muon.medievalorigins.entity.SummonedZombie;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;


public class SummonZombieAction extends EntityAction<FixedSummonTypeConfiguration> {

    public SummonZombieAction() {
        super(FixedSummonTypeConfiguration.CODEC);
    }
    @Override
    public void execute(FixedSummonTypeConfiguration configuration, Entity caster) {
        if (!caster.level().isClientSide() && caster instanceof LivingEntity) {
            ServerLevel serverWorld = (ServerLevel)caster.level();
            ItemStack weapon = configuration.weapon()
                    .map(weaponResource -> BuiltInRegistries.ITEM.getOptional(weaponResource)
                            .map(Item::getDefaultInstance)
                            .orElse(new ItemStack(Items.AIR)))
                    .orElse(new ItemStack(Items.AIR));

            SummonedZombie summon = new SummonedZombie(serverWorld, (LivingEntity) caster, weapon);

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
