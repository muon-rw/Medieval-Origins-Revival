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

public class SpellHealActionType extends BiEntityActionType {
    public static final TypedDataObjectFactory<SpellHealActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("magic_school", SerializableDataTypes.STRING)
                    .add("crit_behavior", SerializableDataTypes.STRING, "normal")
                    .add("base", SerializableDataTypes.FLOAT.optional(), Optional.empty())
                    .add("scaling_factor", SerializableDataTypes.FLOAT, 1.0f)
                    .add("modifier", Modifier.DATA_TYPE, null)
                    .addFunctionedDefault("modifiers", Modifier.LIST_TYPE, data -> MiscUtil.singletonListOrNull(data.get("modifier")))
                    .validate(MiscUtil.validateAnyFieldsPresent("base", "modifier", "modifiers")),
            data -> new SpellHealActionType(
                    data.getString("magic_school"),
                    data.getString("crit_behavior"),
                    data.get("base"),
                    data.getFloat("scaling_factor"),
                    data.get("modifiers")
            ),
            (type, data) -> data.instance()
                    .set("magic_school", type.magicSchool)
                    .set("crit_behavior", type.critBehavior)
                    .set("base", type.baseHealing)
                    .set("scaling_factor", type.scalingFactor)
                    .set("modifiers", type.modifiers)
    );

    private final String magicSchool;
    private final String critBehavior;
    private final Optional<Float> baseHealing;
    private final float scalingFactor;
    private final List<Modifier> modifiers;

    public SpellHealActionType(String magicSchool, String critBehavior, Optional<Float> baseHealing,
                               float scalingFactor, List<Modifier> modifiers) {
        this.magicSchool = magicSchool;
        this.critBehavior = critBehavior;
        this.baseHealing = baseHealing;
        this.scalingFactor = scalingFactor;
        this.modifiers = modifiers;
    }

    @Override
    public void accept(BiEntityActionContext context) {
        Entity actor = context.actor();
        Entity target = context.target();
        if (actor == null || !(target instanceof LivingEntity livingTarget)
                || actor.level().isClientSide() || target.level().isClientSide()) {
            return;
        }

        this.baseHealing
                .or(() -> getModifiedAmount(actor, livingTarget))
                .ifPresent(healing -> {
                    float totalHealing = healing;

                    if (FabricLoader.getInstance().isModLoaded("spell_power")) {
                        try {
                            SpellSchool school = SpellSchoolUtil.getSpellSchoolWithFallback(magicSchool);
                            SpellPower.Result spellPowerResult = SpellPower.getSpellPower(school, (LivingEntity) actor);
                            double finalSpellPower = switch (critBehavior) {
                                case "always" -> spellPowerResult.forcedCriticalValue();
                                case "never" -> spellPowerResult.nonCriticalValue();
                                default -> spellPowerResult.randomValue();
                            };
                            totalHealing += finalSpellPower * scalingFactor;
                        } catch (IllegalArgumentException e) {
                            MedievalOrigins.LOGGER.warn("SpellHealAction: Could not apply spell power scaling for school '{}' (actor: {}, target: {}). Error: {}", magicSchool, actor.getName().getString(), target.getName().getString(), e.getMessage());
                        }
                    }

                    livingTarget.heal(totalHealing);
                });
    }

    private Optional<Float> getModifiedAmount(Entity actor, LivingEntity target) {
        return !modifiers.isEmpty()
                ? Optional.of((float) ModifierUtil.applyModifiers(actor, modifiers, target.getMaxHealth()))
                : Optional.empty();
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ModBientityActionTypes.SPELL_HEAL;
    }
}