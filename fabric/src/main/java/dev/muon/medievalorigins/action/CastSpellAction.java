package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.internals.SpellHelper;

public class CastSpellAction {
    public static void action(SerializableData.Instance data, Entity caster) {
        if (caster instanceof Player) {
            Player player = (Player) caster;
            ItemStack itemStack = player.getMainHandItem();
            SpellHelper.attemptCasting(player, itemStack, data.getId("spell"), data.getBoolean("require_ammo"));
        }
        // todo: run bientity action. pls do this future me ty
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("cast_spell"),
                new SerializableData()
                        .add("spell", SerializableDataTypes.IDENTIFIER)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("require_ammo", SerializableDataTypes.BOOLEAN, null),
                CastSpellAction::action
        );
    }

}
