package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.action.bientity.CureVillagerAction;
import dev.muon.medievalorigins.action.bientity.RaycastBetweenAction;
import dev.muon.medievalorigins.action.bientity.SpellDamageAction;
import dev.muon.medievalorigins.action.bientity.SpellHealAction;
import dev.muon.medievalorigins.action.bientity.TransferItemAction;
import dev.muon.medievalorigins.action.entity.AreaBonemealAction;
import dev.muon.medievalorigins.action.entity.AutoPlantSeedsAction;
import dev.muon.medievalorigins.action.entity.ClearNegativeEffectsAction;
import dev.muon.medievalorigins.action.entity.CommandSummonsAction;
import dev.muon.medievalorigins.action.entity.ModifyDurationAction;
import dev.muon.medievalorigins.action.entity.SummonEntityAction;
import dev.overgrown.apoli.action.ActionTypes;

public final class ModActions {
    private ModActions() {
    }

    public static void register() {
        ActionTypes.ENTITY.register(MedievalOrigins.loc("clear_negative_effects"), new ClearNegativeEffectsAction());
        ActionTypes.ENTITY.register(MedievalOrigins.loc("summon_entity"), new SummonEntityAction());
        ActionTypes.ENTITY.register(MedievalOrigins.loc("command_summons"), new CommandSummonsAction());
        ActionTypes.ENTITY.register(MedievalOrigins.loc("modify_duration"), new ModifyDurationAction());
        ActionTypes.ENTITY.register(MedievalOrigins.loc("area_bonemeal"), new AreaBonemealAction());
        ActionTypes.ENTITY.register(MedievalOrigins.loc("auto_plant_seeds"), new AutoPlantSeedsAction());

        ActionTypes.BI_ENTITY.register(MedievalOrigins.loc("spell_damage"), new SpellDamageAction());
        ActionTypes.BI_ENTITY.register(MedievalOrigins.loc("transfer_item"), new TransferItemAction());
        ActionTypes.BI_ENTITY.register(MedievalOrigins.loc("raycast_between"), new RaycastBetweenAction());
        ActionTypes.BI_ENTITY.register(MedievalOrigins.loc("spell_heal"), new SpellHealAction());
        ActionTypes.BI_ENTITY.register(MedievalOrigins.loc("cure_villager"), new CureVillagerAction());
    }
}
