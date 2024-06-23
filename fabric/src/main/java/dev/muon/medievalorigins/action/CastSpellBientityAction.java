package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.Constants;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.util.SpellCastingUtil;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;

import java.util.List;

public class CastSpellBientityAction {

    public static void action(SerializableData.Instance data, Tuple<Entity, Entity> entities) {
        Entity actor = entities.getA();
        Entity target = entities.getB();

        if (!(actor instanceof Player player) || target == null) {
            return;
        }

        ItemStack itemStack = player.getMainHandItem();
        ResourceLocation spellId = data.getId("spell");
        boolean requireAmmo = data.getBoolean("require_ammo");

        SpellCastingUtil.setBypassesCooldown(true);
        SpellCast.Attempt attempt = SpellHelper.attemptCasting(player, itemStack, spellId, requireAmmo);
        if (!attempt.isSuccess()) {
            Constants.LOG.info("you done fucked up a a ron ");
            return;
        }

        SpellCastingUtil.setRequireAmmo(requireAmmo);
        try {
            if (actor instanceof ServerPlayer) {
                List<Entity> targets = List.of(target);
                SpellHelper.performSpell(player.level(), player, spellId, targets, SpellCast.Action.RELEASE, 1.0f);
            }
        } finally {
            SpellCastingUtil.setRequireAmmo(true);
        }
    }

    public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("cast_spell"),
                new SerializableData()
                        .add("spell", SerializableDataTypes.IDENTIFIER)
                        .add("require_ammo", SerializableDataTypes.BOOLEAN, false),
                CastSpellBientityAction::action
        );
    }
}