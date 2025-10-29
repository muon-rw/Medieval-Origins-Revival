package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class CureVillagerAction {
    public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ActionFactory<>(MedievalOrigins.loc("cure_villager"), new SerializableData(), CureVillagerAction::cureVillager);
    }

    public static void cureVillager(SerializableData.Instance data, Tuple<Entity, Entity> entities) {
        if (entities.getA() instanceof Player player && entities.getB() instanceof ZombieVillager zombieVillager) {
            ItemStack itemInHand = player.getMainHandItem();
            if (itemInHand.is(Items.GOLDEN_APPLE)) {
                if (!zombieVillager.level().isClientSide()) {
                }
            }
        }
    }
}
