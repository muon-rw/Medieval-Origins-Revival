package dev.muon.medievalorigins.action.bientity;

import dev.muon.medievalorigins.action.ModBientityActionTypes;
import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.BiEntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import dev.muon.medievalorigins.util.SpellSchoolUtil;
import dev.muon.medievalorigins.MedievalOrigins;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SpellDamageActionType extends BiEntityActionType {
    public static final TypedDataObjectFactory<SpellDamageActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("magic_school", SerializableDataTypes.STRING)
                    .add("crit_behavior", SerializableDataTypes.STRING, "normal")
                    .add("base", SerializableDataTypes.FLOAT.optional(), Optional.empty())
                    .add("scaling_factor", SerializableDataTypes.FLOAT, 1.0f)
                    .add("modifier", Modifier.DATA_TYPE, null)
                    .addFunctionedDefault("modifiers", Modifier.LIST_TYPE, data -> MiscUtil.singletonListOrNull(data.get("modifier")))
                    .add("damage_type", SerializableDataTypes.DAMAGE_TYPE)
                    .validate(MiscUtil.validateAnyFieldsPresent("base", "modifier", "modifiers")),
            data -> new SpellDamageActionType(
                    data.getString("magic_school"),
                    data.getString("crit_behavior"),
                    data.get("base"),
                    data.getFloat("scaling_factor"),
                    data.get("modifiers"),
                    data.get("damage_type")
            ),
            (type, data) -> data.instance()
                    .set("magic_school", type.magicSchool)
                    .set("crit_behavior", type.critBehavior)
                    .set("base", type.baseDamage)
                    .set("scaling_factor", type.scalingFactor)
                    .set("modifiers", type.modifiers)
                    .set("damage_type", type.damageType)
    );

    private final String magicSchool;
    private final String critBehavior;
    private final Optional<Float> baseDamage;
    private final float scalingFactor;
    private final List<Modifier> modifiers;
    private final ResourceKey<DamageType> damageType;

    public SpellDamageActionType(String magicSchool, String critBehavior, Optional<Float> baseDamage,
                                 float scalingFactor, List<Modifier> modifiers, ResourceKey<DamageType> damageType) {
        this.magicSchool = magicSchool;
        this.critBehavior = critBehavior;
        this.baseDamage = baseDamage;
        this.scalingFactor = scalingFactor;
        this.modifiers = modifiers;
        this.damageType = damageType;
    }

    @Override
    public void accept(BiEntityActionContext context) {
        Entity actor = context.actor();
        Entity target = context.target();
        if (actor == null || target == null || actor.level().isClientSide() || target.level().isClientSide()) {
            return;
        }

        this.baseDamage
                .or(() -> getModifiedAmount(actor, target))
                .ifPresent(damage -> {
                    float totalDamage = damage;

                    if (FabricLoader.getInstance().isModLoaded("spell_power")) {
                        try {
                            SpellSchool school = SpellSchoolUtil.getSpellSchoolWithFallback(magicSchool);
                            SpellPower.Result spellPowerResult = SpellPower.getSpellPower(school, (LivingEntity) actor);
                            double finalSpellPower = switch (critBehavior) {
                                case "always" -> spellPowerResult.forcedCriticalValue();
                                case "never" -> spellPowerResult.nonCriticalValue();
                                default -> spellPowerResult.randomValue();
                            };
                            totalDamage += finalSpellPower * scalingFactor;
                        } catch (IllegalArgumentException e) {
                            MedievalOrigins.LOGGER.warn("SpellDamageAction: Could not apply spell power scaling for school '{}' (actor: {}, target: {}). Error: {}", magicSchool, actor.getName().getString(), target.getName().getString(), e.getMessage());
                        }
                    }

                    target.hurt(actor.damageSources().source(this.damageType, actor), totalDamage);
                });
    }

    private Optional<Float> getModifiedAmount(Entity actor, Entity target) {
        return !modifiers.isEmpty() && target instanceof LivingEntity livingTarget
                ? Optional.of((float) ModifierUtil.applyModifiers(actor, modifiers, livingTarget.getMaxHealth()))
                : Optional.empty();
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModBientityActionTypes.SPELL_DAMAGE;
    }
}