package dev.muon.medievalorigins.action.bientity;

import dev.muon.medievalorigins.action.ModBientityActionTypes;
import dev.muon.medievalorigins.action.entity.ClearNegativeEffectsActionType;
import dev.muon.medievalorigins.mixin.ZombieVillagerInvoker;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.BiEntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class CureVillagerActionType extends BiEntityActionType {
    public static final TypedDataObjectFactory<CureVillagerActionType> DATA_FACTORY;

    @Override
    public void accept(BiEntityActionContext context) {
        if (context.actor() instanceof Player player && context.target() instanceof ZombieVillager zombieVillager) {

            if (!zombieVillager.level().isClientSide()) {
                ((ZombieVillagerInvoker) zombieVillager).callStartConverting(player.getUUID(), 1);
            }

        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModBientityActionTypes.CURE_VILLAGER;
    }

    static {
        DATA_FACTORY = TypedDataObjectFactory.simple(
                new SerializableData(),
                data -> new CureVillagerActionType(),
                (actionType, serializableData) -> serializableData.instance()
        );
    }
}
