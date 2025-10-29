package dev.muon.medievalorigins.platform;

import dev.muon.medievalorigins.compat.SpellEngineUtils;
import dev.muon.medievalorigins.platform.services.ISpellHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;

import java.util.Optional;

/**
 * Fabric implementation using Spell Engine and Spell Power mods.
 */
public class FabricSpellPowerHelper implements ISpellHelper {

    @Override
    public double getSpellPower(String magicSchool, LivingEntity actor, String critBehavior) {
        if (!FabricLoader.getInstance().isModLoaded("spell_power")) {
            return 0.0;
        }

        try {
            SpellSchool school = SpellSchools.getSchool(magicSchool);
            if (school == null) {
                return 0.0;
            }

            SpellPower.Result spellPowerResult = SpellPower.getSpellPower(school, actor);
            return switch (critBehavior) {
                case "always" -> spellPowerResult.forcedCriticalValue();
                case "never" -> spellPowerResult.nonCriticalValue();
                default -> spellPowerResult.randomValue();
            };
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public void castSpell(Entity entity, ResourceLocation spellId, 
                          boolean requireAmmo, String targetType, float range,
                          int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost, 
                          boolean continuousCost, int costInterval) {
        if (!FabricLoader.getInstance().isModLoaded("spell_engine")) {
            return;
        }

        // Spell Engine uses: requireAmmo, targetType, range
        // Ignores Iron's Spellbooks parameters: powerLevel, castTime, manaCost, continuousCost, costInterval
        SpellEngineUtils.castSpell(entity, spellId, requireAmmo, targetType, range);
    }

    @Override
    public void castSpellWithTarget(Entity actor, Entity target, ResourceLocation spellId,
                                    boolean requireAmmo,
                                    int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost,
                                    boolean continuousCost, int costInterval) {
        if (!FabricLoader.getInstance().isModLoaded("spell_engine")) {
            return;
        }

        // Spell Engine uses: requireAmmo
        // Ignores Iron's Spellbooks parameters: powerLevel, castTime, manaCost, continuousCost, costInterval
        SpellEngineUtils.castSpellOnTarget(actor, target, spellId, requireAmmo);
    }

    @Override
    public String getSpellSystem() {
        return FabricLoader.getInstance().isModLoaded("spell_engine") ? "spell_engine" : "none";
    }
}

