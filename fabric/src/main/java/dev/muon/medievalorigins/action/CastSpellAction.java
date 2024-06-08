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
    public static void castAction(Player player, String spellIdentifier) {
        ItemStack itemStack = player.getMainHandItem();
        ResourceLocation spellID = new ResourceLocation(spellIdentifier);
        SpellHelper.attemptCasting(player, itemStack, spellID, false);
    }
    public static void action(SerializableData.Instance data, Entity caster) {
}

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("cast_spell"),
                new SerializableData()
                        .add("spell", SerializableDataTypes.IDENTIFIER)
                        .add("tag", SerializableDataTypes.NBT, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null),
                CastSpellAction::action
        );
    }

}
