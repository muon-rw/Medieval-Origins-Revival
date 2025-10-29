package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;

import java.util.Optional;



public class CastSpellAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (entity.level().isClientSide) return;
        
        ResourceLocation spellId = data.getId("spell");
        
        // Spell Engine specific parameters
        boolean requireAmmo = data.getBoolean("require_ammo");
        String targetType = data.getString("target_type");
        float range = data.getFloat("range");
        
        // Iron's Spellbooks specific parameters
        int powerLevel = data.getInt("power_level");
        Optional<Integer> castTime = data.isPresent("cast_time") ? Optional.of(data.getInt("cast_time")) : Optional.empty();
        Optional<Integer> manaCost = data.isPresent("mana_cost") ? Optional.of(data.getInt("mana_cost")) : Optional.empty();
        boolean continuousCost = data.getBoolean("continuous_cost");
        int costInterval = data.getInt("cost_interval");
        
        // Use the platform service to cast the spell
        Services.SPELL_POWER.castSpell(entity, spellId, requireAmmo, targetType, range, 
                                       powerLevel, castTime, manaCost, continuousCost, costInterval);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("cast_spell"),
                new SerializableData()
                        .add("spell", SerializableDataTypes.IDENTIFIER) // Platform-agnostic
                        // Spell Engine specific parameters
                        .add("require_ammo", SerializableDataTypes.BOOLEAN, false)
                        .add("target_type", SerializableDataTypes.STRING, "raycast")
                        .add("range", SerializableDataTypes.FLOAT, 20.0f)
                        // Iron's Spellbooks specific parameters
                        .add("power_level", SerializableDataTypes.INT, 1)
                        .add("cast_time", SerializableDataTypes.INT)
                        .add("mana_cost", SerializableDataTypes.INT)
                        .add("continuous_cost", SerializableDataTypes.BOOLEAN, false)
                        .add("cost_interval", SerializableDataTypes.INT, 20),
                CastSpellAction::action
        );
    }
}