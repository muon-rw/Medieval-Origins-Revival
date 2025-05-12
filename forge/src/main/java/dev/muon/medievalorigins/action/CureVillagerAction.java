package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.mixin.ZombieVillagerInvoker;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BiConsumer;

public class CureVillagerAction extends BiEntityAction<NoConfiguration> {

    public static void cureVillager(Entity actor, Entity target) {
        if (actor instanceof Player player && target instanceof ZombieVillager zombieVillager) {
            ItemStack itemInHand = player.getMainHandItem();
            if (itemInHand.is(Items.GOLDEN_APPLE)) {
                if (!zombieVillager.level().isClientSide()) {
                    ((ZombieVillagerInvoker) zombieVillager).callStartConverting(player.getUUID(), 1);
                }
            }
        }
    }

    private final BiConsumer<Entity, Entity> action;

    public CureVillagerAction(BiConsumer<Entity, Entity> action) {
        super(NoConfiguration.CODEC);
        this.action = action;
    }

    @Override
    public void execute(NoConfiguration configuration, Entity actor, Entity target) {
        this.action.accept(actor, target);
    }
} 