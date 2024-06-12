package dev.muon.medievalorigins.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.DamageSourceDescription;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Cursed, don't look
 */
public class SpellDamageAction extends BiEntityAction<SpellDamageAction.Configuration> {

    private static final Map<String, String> SCHOOL_TRANSLATION_MAP = new HashMap<>();

    static {
        SCHOOL_TRANSLATION_MAP.put("frost", "ice");
        SCHOOL_TRANSLATION_MAP.put("healing", "holy");
        SCHOOL_TRANSLATION_MAP.put("arcane", "ender");
        SCHOOL_TRANSLATION_MAP.put("soul", "blood");
    }

    public SpellDamageAction() {
        super(Configuration.CODEC);
    }

    @Override
    public void execute(Configuration configuration, Entity actor, Entity target) {
        if (!(actor instanceof LivingEntity) || target == null) {
            return;
        }

        float baseDamage = configuration.base();
        String magicSchoolStr = configuration.magicSchool();
        ResourceLocation schoolResourceLocation = new ResourceLocation(magicSchoolStr.contains(":") ? magicSchoolStr : "irons_spellbooks:" + magicSchoolStr);
        SchoolType magicSchool = SchoolRegistry.getSchool(schoolResourceLocation);

        if (magicSchool == null) {
            String translatedSchool = SCHOOL_TRANSLATION_MAP.get(magicSchoolStr);
            if (translatedSchool != null) {
                schoolResourceLocation = new ResourceLocation(translatedSchool.contains(":") ? translatedSchool : "irons_spellbooks:" + translatedSchool);
                magicSchool = SchoolRegistry.getSchool(schoolResourceLocation);
            }
        }

        if (magicSchool == null) {
            System.out.println("No valid Magic School found for type " + magicSchoolStr);
            return;
        }

        double spellPower = magicSchool.getPowerFor((LivingEntity) actor);
        baseDamage += spellPower * configuration.scalingFactor();

        DamageSource source = MiscUtil.createDamageSource(
                actor.damageSources(),
                configuration.source().orElse(null),
                configuration.damageType().orElse(null),
                actor);

        target.hurt(source, baseDamage);
    }

    public static class Configuration implements IDynamicFeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("base").forGetter(Configuration::base),
                Codec.STRING.fieldOf("magic_school").forGetter(Configuration::magicSchool),
                Codec.FLOAT.fieldOf("scaling_factor").forGetter(Configuration::scalingFactor),
                CalioCodecHelper.optionalField(ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, "source").forGetter(Configuration::source),
                CalioCodecHelper.optionalField(SerializableDataTypes.DAMAGE_TYPE, "damage_type").forGetter(Configuration::damageType)
        ).apply(instance, Configuration::new));

        private final float base;
        private final String magicSchool;
        private final float scalingFactor;
        private final Optional<DamageSourceDescription> source;
        private final Optional<ResourceKey<DamageType>> damageType;

        public Configuration(float base, String magicSchool, float scalingFactor, Optional<DamageSourceDescription> source, Optional<ResourceKey<DamageType>> damageType) {
            this.base = base;
            this.magicSchool = magicSchool;
            this.scalingFactor = scalingFactor;
            this.source = source;
            this.damageType = damageType;
        }

        public float base() {
            return base;
        }

        public String magicSchool() {
            return magicSchool;
        }

        public float scalingFactor() {
            return scalingFactor;
        }

        public Optional<DamageSourceDescription> source() {
            return source;
        }

        public Optional<ResourceKey<DamageType>> damageType() {
            return damageType;
        }
    }
}