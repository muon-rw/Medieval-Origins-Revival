package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.spell_power.api.SpellSchools;


public class SpellDamageAction {
    public SpellDamageAction() {
    }

    public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ActionFactory<>(MedievalOrigins.loc("spell_damage"),
                new SerializableData()
                        .add("magic_school", SerializableDataTypes.STRING)
                        .add("crit_behavior", SerializableDataTypes.STRING, "normal")
                        .add("base", SerializableDataTypes.FLOAT)
                        .add("scaling_factor", SerializableDataTypes.FLOAT)
                        .add("source", ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, null)
                        .add("damage_type", SerializableDataTypes.DAMAGE_TYPE),
                SpellDamageAction::action);
    }

    public static void action(SerializableData.Instance data, Tuple<Entity, Entity> entities) {
        Entity actor = entities.getA();
        Entity target = entities.getB();

        if (!(actor instanceof LivingEntity) || target == null) {
            return;
        }

        String magicSchoolStr = data.get("magic_school");
        SpellSchool magicSchool;
        try {
            magicSchool = SpellSchools.getSchool(magicSchoolStr);
            if (magicSchool == null) {
                throw new IllegalArgumentException("Unknown magic school: " + magicSchoolStr);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown magic school: " + magicSchoolStr);
        }

        String critBehavior = data.get("crit_behavior");
        SpellPower.Result spellPowerResult = SpellPower.getSpellPower(magicSchool, (LivingEntity) actor);
        double finalSpellPower;
        switch (critBehavior) {
            case "always":
                finalSpellPower = spellPowerResult.forcedCriticalValue();
                break;
            case "never":
                finalSpellPower = spellPowerResult.nonCriticalValue();
                break;
            case "normal":
            default:
                finalSpellPower = spellPowerResult.randomValue();
                break;
        }

        float baseDamage = data.get("base");
        float scalingFactor = data.get("scaling_factor");
        double totalDamage = baseDamage + (finalSpellPower * scalingFactor);

        DamageSource source = MiscUtil.createDamageSource(
                actor.damageSources(),
                data.get("source"),
                data.get("damage_type"),
                actor);

        target.hurt(source, (float) totalDamage);
    }
}