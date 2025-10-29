package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.action.entity.*;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Registry;

public class ModEntityActions {
    public static void register() {
        register(SummonEntityAction.getFactory());
        register(CastSpellAction.getFactory());
        register(ClearNegativeEffectsAction.getFactory());
        register(AreaBonemealAction.getFactory());
        register(AutoPlantSeedsAction.getFactory());
        register(CommandSummonsAction.getFactory());
    }

    public static ActionFactory<Entity> register(ActionFactory<Entity> actionFactory) {
        return Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}