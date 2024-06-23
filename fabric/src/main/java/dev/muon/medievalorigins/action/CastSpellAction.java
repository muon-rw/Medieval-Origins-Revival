package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.utils.TargetHelper;

import java.util.List;



public class CastSpellAction {
    private static boolean requireAmmo = true;

    public static boolean requiresAmmo() {
        return requireAmmo;
    }
    public static void setRequireAmmo(boolean value) {
        requireAmmo = value;
    }
    public static void action(SerializableData.Instance data, Entity entity) {
        if (entity instanceof Player player) {
            ItemStack itemStack = player.getMainHandItem();
            ResourceLocation spellId = data.getId("spell");
            boolean requireAmmoField = data.getBoolean("require_ammo");
            String targetType = data.getString("target_type");
            float range = data.getFloat("range");

            SpellCast.Attempt attempt = SpellHelper.attemptCasting(player, itemStack, spellId, requireAmmoField);
            if (!attempt.isSuccess()) {
                return;
            }
            setRequireAmmo(requireAmmoField);
            if (entity instanceof ServerPlayer) {
                List<Entity> targets = switch (targetType) {
                    case "area" ->
                            TargetHelper.targetsFromArea(player, range, new Spell.Release.Target.Area(), e -> e instanceof LivingEntity);
                    case "raycast" -> {
                        Entity target = TargetHelper.targetFromRaycast(player, range, e -> e instanceof LivingEntity);
                        yield target != null ? List.of(target) : List.of();
                    }
                    case "raycast_multiple" ->
                            TargetHelper.targetsFromRaycast(player, range, e -> e instanceof LivingEntity);
                    default -> List.of();
                };

                if (!targets.isEmpty()) {
                    SpellHelper.performSpell(player.level(), player, spellId, targets, SpellCast.Action.RELEASE, 1.0f);
                }
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("cast_spell"),
                new SerializableData()
                        .add("spell", SerializableDataTypes.IDENTIFIER)
                        .add("require_ammo", SerializableDataTypes.BOOLEAN, false)
                        .add("target_type", SerializableDataTypes.STRING, "raycast")
                        .add("range", SerializableDataTypes.FLOAT, 20.0f),
                CastSpellAction::action
        );
    }
}