package dev.muon.medievalorigins.util;

import net.minecraft.world.entity.LivingEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;

import java.util.Map;

public final class SpellSchoolUtil {
    private static final Map<String, String> FALLBACK_MAP = Map.of(
            "earth", "healing",
            "air", "lightning",
            "water", "frost"
    );

    private SpellSchoolUtil() {
    }

    /**
     * Spell Power 1.6.0 returns null for unknown schools (older versions threw), so both are handled.
     *
     * @throws IllegalArgumentException if the name is empty, or neither it nor its fallback resolves
     */
    public static SpellSchool getSpellSchoolWithFallback(String schoolName) {
        if (schoolName == null || schoolName.isEmpty()) {
            throw new IllegalArgumentException("School name cannot be null or empty.");
        }

        SpellSchool school = lookup(schoolName);
        if (school != null) {
            return school;
        }

        String fallbackSchoolName = FALLBACK_MAP.get(schoolName.toLowerCase());
        if (fallbackSchoolName == null) {
            throw new IllegalArgumentException("Unknown magic school: \"" + schoolName
                    + "\" (not found or resolved to null, and no fallback defined).");
        }

        school = lookup(fallbackSchoolName);
        if (school == null) {
            throw new IllegalArgumentException("Unknown magic school: \"" + schoolName
                    + "\". Fallback school \"" + fallbackSchoolName + "\" resolved to null.");
        }
        return school;
    }

    public static double getSpellPowerValue(LivingEntity entity, String schoolName, String critBehavior) {
        SpellPower.Result result = SpellPower.getSpellPower(getSpellSchoolWithFallback(schoolName), entity);
        return switch (critBehavior) {
            case "always" -> result.forcedCriticalValue();
            case "never" -> result.nonCriticalValue();
            default -> result.randomValue();
        };
    }

    private static SpellSchool lookup(String schoolName) {
        try {
            return SpellSchools.getSchool(schoolName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
