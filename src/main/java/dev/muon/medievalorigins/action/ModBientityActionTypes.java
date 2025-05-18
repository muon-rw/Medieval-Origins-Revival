package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.action.bientity.*;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.util.IdentifierAlias;
import net.minecraft.core.Registry;

public class ModBientityActionTypes {
    public static final IdentifierAlias ALIASES = new IdentifierAlias();
    public static final SerializableDataType<ActionConfiguration<BiEntityActionType>> DATA_TYPE = SerializableDataType.registry(
            ApoliRegistries.BIENTITY_ACTION_TYPE,
            MedievalOrigins.MOD_ID,
            ALIASES,
            (configurations, id) -> "Bi-entity action type \"" + id + "\" is undefined!"
    );

    public static final ActionConfiguration<SpellDamageActionType> SPELL_DAMAGE = register(ActionConfiguration.of(MedievalOrigins.loc("spell_damage"), SpellDamageActionType.DATA_FACTORY));
    public static final ActionConfiguration<TransferItemActionType> TRANSFER_ITEM = register(ActionConfiguration.of(MedievalOrigins.loc("transfer_item"), TransferItemActionType.DATA_FACTORY));
    public static final ActionConfiguration<RaycastBetweenActionType> RAYCAST_BETWEEN = register(ActionConfiguration.of(MedievalOrigins.loc("raycast_between"), RaycastBetweenActionType.DATA_FACTORY));
    public static final ActionConfiguration<SpellHealActionType> SPELL_HEAL = register(ActionConfiguration.of(MedievalOrigins.loc("spell_heal"), SpellHealActionType.DATA_FACTORY));
    public static final ActionConfiguration<CureVillagerActionType> CURE_VILLAGER = register(ActionConfiguration.of(MedievalOrigins.loc("cure_villager"), CureVillagerActionType.DATA_FACTORY));

    public static void register() {}

    @SuppressWarnings("unchecked")
    public static <T extends BiEntityActionType> ActionConfiguration<T> register(ActionConfiguration<T> configuration) {
        ActionConfiguration<BiEntityActionType> casted = (ActionConfiguration<BiEntityActionType>) configuration;
        Registry.register(ApoliRegistries.BIENTITY_ACTION_TYPE, casted.id(), casted);
        return configuration;
    }
}