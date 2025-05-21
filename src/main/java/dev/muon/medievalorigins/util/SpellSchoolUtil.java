package dev.muon.medievalorigins.util;

import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import java.util.Map;
import java.util.HashMap;

public class SpellSchoolUtil {
    private static final Map<String, String> FALLBACK_MAP = new HashMap<>();

    static {
        FALLBACK_MAP.put("earth", "healing");
        FALLBACK_MAP.put("air", "lightning");
        FALLBACK_MAP.put("water", "frost");
    }

    /**
     * Retrieves a SpellSchool, attempting a direct lookup first, then using a predefined fallback map
     * if the initial lookup fails or returns null.
     *
     * @param schoolName The name of the spell school to retrieve.
     * @return The resolved SpellSchool.
     * @throws IllegalArgumentException if the school name is null/empty, or if both the original
     *                                  and any applicable fallback school name cannot be resolved.
     */
    public static SpellSchool getSpellSchoolWithFallback(String schoolName) {
        if (schoolName == null || schoolName.isEmpty()) {
            throw new IllegalArgumentException("School name cannot be null or empty.");
        }

        SpellSchool school = null;
        String originalSchoolNameForError = schoolName;
        String currentSchoolNameToTry = schoolName;

        try {
            school = SpellSchools.getSchool(currentSchoolNameToTry);
        } catch (IllegalArgumentException e) {
            // Proceed to fallback if applicable.
        }

        if (school == null) {
            String fallbackKey = schoolName.toLowerCase();
            String fallbackSchoolName = FALLBACK_MAP.get(fallbackKey);

            if (fallbackSchoolName != null) {
                currentSchoolNameToTry = fallbackSchoolName;
                try {
                    school = SpellSchools.getSchool(currentSchoolNameToTry);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Unknown magic school: \"" + originalSchoolNameForError +
                            "\". Fallback school \"" + currentSchoolNameToTry + "\" also failed to parse: " + e.getMessage(), e);
                }
                if (school == null) {
                     throw new IllegalArgumentException("Unknown magic school: \"" + originalSchoolNameForError +
                            "\". Fallback school \"" + currentSchoolNameToTry + "\" resolved to null.");
                }
            } else {
                throw new IllegalArgumentException("Unknown magic school: \"" + originalSchoolNameForError +
                        "\" (not found or resolved to null, and no fallback defined).");
            }
        }
        return school;
    }
} 