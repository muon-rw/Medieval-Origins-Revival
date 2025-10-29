package dev.muon.medievalorigins.platform;

import dev.muon.medievalorigins.compat.IronsSpellbooksUtils;
import dev.muon.medievalorigins.platform.services.ISpellHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

/**
 * Forge implementation using Iron's Spellbooks.
 */
public class ForgeSpellPowerHelper implements ISpellHelper {

    @Override
    public double getSpellPower(String magicSchool, LivingEntity actor, String critBehavior) {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return 0.0;
        }

        return IronsSpellbooksUtils.getSpellPowerForSchool(magicSchool, actor);
    }

    @Override
    public void castSpell(Entity entity, ResourceLocation spellId, 
                          boolean requireAmmo, String targetType, float range,
                          int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost, 
                          boolean continuousCost, int costInterval) {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return;
        }

        // Iron's Spellbooks uses: powerLevel, castTime, manaCost, continuousCost, costInterval
        // Ignores Spell Engine parameters: requireAmmo, targetType, range
        IronsSpellbooksUtils.castSpell(entity, spellId, powerLevel, castTime, manaCost, continuousCost, costInterval);
    }

    @Override
    public void castSpellWithTarget(Entity actor, Entity target, ResourceLocation spellId,
                                    boolean requireAmmo,
                                    int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost,
                                    boolean continuousCost, int costInterval) {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return;
        }

        // Iron's Spellbooks uses: powerLevel, castTime, manaCost, continuousCost, costInterval
        // Ignores Spell Engine parameters: requireAmmo
        IronsSpellbooksUtils.castSpellOnTarget(actor, target, spellId, powerLevel, castTime, manaCost, continuousCost, costInterval);
    }

    @Override
    public String getSpellSystem() {
        return ModList.get().isLoaded("irons_spellbooks") ? "irons_spellbooks" : "none";
    }
}

