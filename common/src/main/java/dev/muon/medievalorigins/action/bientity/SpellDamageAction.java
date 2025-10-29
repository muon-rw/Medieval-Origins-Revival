package dev.muon.medievalorigins.action.bientity;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
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

        float baseDamage = data.get("base");
        float scalingFactor = data.get("scaling_factor");
        String magicSchool = data.get("magic_school");
        String critBehavior = data.get("crit_behavior");

        double spellPower = Services.SPELL_POWER.getSpellPower(magicSchool, (LivingEntity) actor, critBehavior);
        double totalDamage = baseDamage + (spellPower * scalingFactor);

        DamageSource source = MiscUtil.createDamageSource(
                actor.damageSources(),
                data.get("source"),
                data.get("damage_type"),
                actor);

        target.hurt(source, (float) totalDamage);
    }

}