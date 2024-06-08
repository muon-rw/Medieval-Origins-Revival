package dev.muon.medievalorigins.action;
import io.github.edwinmindcraft.apoli.api.power.factory.*;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModActions {
    public static final DeferredRegister<EntityAction<?>> ENTITY_ACTIONS = DeferredRegister.create(ApoliRegistries.ENTITY_ACTION_KEY, MedievalOrigins.MODID);
    public static final RegistryObject<SummonSkeletonAction> SUMMON_SKELETON = ENTITY_ACTIONS.register("summon_skeleton", SummonSkeletonAction::new);
    public static final RegistryObject<SummonWitherSkeletonAction> SUMMON_WITHER_SKELETON = ENTITY_ACTIONS.register("summon_wither_skeleton", SummonWitherSkeletonAction::new);
    public static final RegistryObject<SummonZombieAction> SUMMON_ZOMBIE = ENTITY_ACTIONS.register("summon_zombie", SummonZombieAction::new);


    public static final DeferredRegister<BiEntityAction<?>> BIENTITY_ACTIONS = DeferredRegister.create(ApoliRegistries.BIENTITY_ACTION_KEY, MedievalOrigins.MODID);
    public static final RegistryObject<TransferItemAction> TRANSFER_ITEM = BIENTITY_ACTIONS.register("transfer_item", () -> {
        return new TransferItemAction(TransferItemAction::transferItem);
    });
    public static void register(IEventBus eventBus) {
        ENTITY_ACTIONS.register(eventBus);
        BIENTITY_ACTIONS.register(eventBus);
    }
}
