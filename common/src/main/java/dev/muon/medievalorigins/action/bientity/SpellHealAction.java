package dev.muon.medievalorigins.action.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
import dev.muon.medievalorigins.util.SpellSchoolUtil;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import dev.overgrown.apoli.data.AttributeModifier;
import dev.overgrown.apoli.data.AttributeModifierHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public final class SpellHealAction implements ActionType<BiEntityCtx, SpellHealAction.Cfg> {

    public record Cfg(
            String magicSchool,
            String critBehavior,
            Optional<Float> base,
            float scalingFactor,
            Optional<AttributeModifier> modifier,
            Optional<List<AttributeModifier>> modifiers
    ) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.<Cfg>mapCodec(i -> i.group(
            Codec.STRING.fieldOf("magic_school").forGetter(Cfg::magicSchool),
            Codec.STRING.optionalFieldOf("crit_behavior", "normal").forGetter(Cfg::critBehavior),
            Codec.FLOAT.optionalFieldOf("base").forGetter(Cfg::base),
            Codec.FLOAT.optionalFieldOf("scaling_factor", 1.0f).forGetter(Cfg::scalingFactor),
            AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(Cfg::modifier),
            AttributeModifier.LIST_OR_SINGLE.optionalFieldOf("modifiers").forGetter(Cfg::modifiers)
    ).apply(i, Cfg::new)).flatXmap(SpellHealAction::validate, DataResult::success);

    private static DataResult<Cfg> validate(Cfg cfg) {
        return cfg.base().isEmpty() && cfg.modifier().isEmpty() && cfg.modifiers().isEmpty()
                ? DataResult.error(() -> "spell_heal requires at least one of 'base', 'modifier' or 'modifiers'")
                : DataResult.success(cfg);
    }

    @Override
    public MapCodec<Cfg> codec() {
        return CODEC;
    }

    @Override
    public void run(Cfg cfg, BiEntityCtx ctx) {
        Entity actor = ctx.actor();
        Entity target = ctx.target();
        if (actor == null || !(target instanceof LivingEntity livingTarget)
                || actor.level().isClientSide() || target.level().isClientSide()) {
            return;
        }

        cfg.base()
                .or(() -> getModifiedAmount(cfg, actor, livingTarget))
                .ifPresent(healing -> {
                    float totalHealing = healing;

                    if (actor instanceof LivingEntity livingActor && Services.PLATFORM.isModLoaded("spell_power")) {
                        try {
                            totalHealing += (float) (SpellSchoolUtil.getSpellPowerValue(livingActor, cfg.magicSchool(), cfg.critBehavior()) * cfg.scalingFactor());
                        } catch (IllegalArgumentException e) {
                            MedievalOrigins.LOGGER.warn("SpellHealAction: Could not apply spell power scaling for school '{}' (actor: {}, target: {}). Error: {}", cfg.magicSchool(), actor.getName().getString(), target.getName().getString(), e.getMessage());
                        }
                    }

                    livingTarget.heal(totalHealing);
                });
    }

    private static Optional<Float> getModifiedAmount(Cfg cfg, Entity actor, LivingEntity target) {
        List<AttributeModifier> modifiers = AttributeModifierHelper.flatten(cfg.modifier(), cfg.modifiers());
        return !modifiers.isEmpty()
                ? Optional.of(AttributeModifierHelper.apply(target.getMaxHealth(), modifiers, actor))
                : Optional.empty();
    }
}
