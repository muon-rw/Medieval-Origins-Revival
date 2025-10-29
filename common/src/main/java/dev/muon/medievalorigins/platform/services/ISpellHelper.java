package dev.muon.medievalorigins.platform.services;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Platform service for spell power and spell casting systems.
 * Implementations handle Spell Engine (Fabric) and Iron's Spellbooks (Forge).
 */
public interface ISpellHelper {

    /**
     * Calculates spell power/damage scaling for a magic school.
     * 
     * @param magicSchool The magic school identifier
     * @param actor The entity casting the spell
     * @param critBehavior Critical hit behavior: "normal", "always", or "never"
     * @return The spell power value, or 0.0 if not supported
     */
    double getSpellPower(String magicSchool, LivingEntity actor, String critBehavior);

    /**
     * Casts a spell without explicit targeting (uses spell system's default behavior).
     * 
     * @param entity The entity casting the spell
     * @param spellId The spell resource location
     * @param requireAmmo Spell Engine: Whether to require ammo
     * @param targetType Spell Engine: Target type ("raycast", "area", "raycast_multiple")
     * @param range Spell Engine: Targeting range
     * @param powerLevel Iron's Spellbooks: Spell power level
     * @param castTime Iron's Spellbooks: Override cast time (empty = use default)
     * @param manaCost Iron's Spellbooks: Override mana cost (empty = use default)
     * @param continuousCost Iron's Spellbooks: Whether to apply continuous mana drain
     * @param costInterval Iron's Spellbooks: Tick interval for continuous cost
     */
    void castSpell(Entity entity, ResourceLocation spellId, 
                   boolean requireAmmo, String targetType, float range,
                   int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost, 
                   boolean continuousCost, int costInterval);

    /**
     * Casts a spell with a specific target entity (for bientity actions).
     * 
     * @param actor The entity casting the spell
     * @param target The target entity
     * @param spellId The spell resource location
     * @param requireAmmo Spell Engine: Whether to require ammo
     * @param powerLevel Iron's Spellbooks: Spell power level
     * @param castTime Iron's Spellbooks: Override cast time (empty = use default)
     * @param manaCost Iron's Spellbooks: Override mana cost (empty = use default)
     * @param continuousCost Iron's Spellbooks: Whether to apply continuous mana drain
     * @param costInterval Iron's Spellbooks: Tick interval for continuous cost
     */
    void castSpellWithTarget(Entity actor, Entity target, ResourceLocation spellId,
                             boolean requireAmmo,
                             int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost,
                             boolean continuousCost, int costInterval);

    /**
     * Returns the name of the spell system this helper supports.
     * 
     * @return "spell_engine", "irons_spellbooks", or "none"
     */
    String getSpellSystem();
}

