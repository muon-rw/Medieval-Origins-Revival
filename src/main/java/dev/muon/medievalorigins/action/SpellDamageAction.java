package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import net.spell_power.api.SpellPower;
import net.spell_power.api.MagicSchool;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;


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
        MagicSchool magicSchool;
        try {
            magicSchool = MagicSchool.valueOf(magicSchoolStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown magic school: " + magicSchoolStr);
        }

        String critBehaviorStr = data.get("crit_behavior");
        double spellPowerResult;
        var result = SpellPower.getSpellPower(magicSchool, (LivingEntity) actor);
        switch (critBehaviorStr) {
            case "always":
                spellPowerResult = result.forcedCriticalValue();
                break;
            case "never":
                spellPowerResult = result.nonCriticalValue();
                break;
            case "normal":
            default:
                spellPowerResult = result.randomValue();
                break;
        }

        float baseDamage = data.get("base");
        float scalingFactor = data.get("scaling_factor");
        double totalDamage = baseDamage + (spellPowerResult * scalingFactor);

        // ResourceLocation damageType = magicSchool.damageTypeId(); // nvm
        DamageSource source = MiscUtil.createDamageSource(
                actor.damageSources(),
                data.get("source"),
                data.get("damage_type"),
                actor);

        target.hurt(source, (float) totalDamage);
    }
}