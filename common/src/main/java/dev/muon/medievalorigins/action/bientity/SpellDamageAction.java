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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public final class SpellDamageAction implements ActionType<BiEntityCtx, SpellDamageAction.Cfg> {

    public record Cfg(
            String magicSchool,
            String critBehavior,
            Optional<Float> base,
            float scalingFactor,
            Optional<AttributeModifier> modifier,
            Optional<List<AttributeModifier>> modifiers,
            ResourceKey<DamageType> damageType
    ) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.<Cfg>mapCodec(i -> i.group(
            Codec.STRING.fieldOf("magic_school").forGetter(Cfg::magicSchool),
            Codec.STRING.optionalFieldOf("crit_behavior", "normal").forGetter(Cfg::critBehavior),
            Codec.FLOAT.optionalFieldOf("base").forGetter(Cfg::base),
            Codec.FLOAT.optionalFieldOf("scaling_factor", 1.0f).forGetter(Cfg::scalingFactor),
            AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(Cfg::modifier),
            AttributeModifier.LIST_OR_SINGLE.optionalFieldOf("modifiers").forGetter(Cfg::modifiers),
            ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("damage_type").forGetter(Cfg::damageType)
    ).apply(i, Cfg::new)).flatXmap(SpellDamageAction::validate, DataResult::success);

    private static DataResult<Cfg> validate(Cfg cfg) {
        return cfg.base().isEmpty() && cfg.modifier().isEmpty() && cfg.modifiers().isEmpty()
                ? DataResult.error(() -> "spell_damage requires at least one of 'base', 'modifier' or 'modifiers'")
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
        if (actor == null || target == null || actor.level().isClientSide() || target.level().isClientSide()) {
            return;
        }

        cfg.base()
                .or(() -> getModifiedAmount(cfg, actor, target))
                .ifPresent(damage -> {
                    float totalDamage = damage;

                    if (actor instanceof LivingEntity livingActor && Services.PLATFORM.isModLoaded("spell_power")) {
                        try {
                            totalDamage += (float) (SpellSchoolUtil.getSpellPowerValue(livingActor, cfg.magicSchool(), cfg.critBehavior()) * cfg.scalingFactor());
                        } catch (IllegalArgumentException e) {
                            MedievalOrigins.LOGGER.warn("SpellDamageAction: Could not apply spell power scaling for school '{}' (actor: {}, target: {}). Error: {}", cfg.magicSchool(), actor.getName().getString(), target.getName().getString(), e.getMessage());
                        }
                    }

                    Holder<DamageType> type = actor.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(cfg.damageType());
                    target.hurt(new DamageSource(type, actor), totalDamage);
                });
    }

    private static Optional<Float> getModifiedAmount(Cfg cfg, Entity actor, Entity target) {
        List<AttributeModifier> modifiers = AttributeModifierHelper.flatten(cfg.modifier(), cfg.modifiers());
        return !modifiers.isEmpty() && target instanceof LivingEntity livingTarget
                ? Optional.of(AttributeModifierHelper.apply(livingTarget.getMaxHealth(), modifiers, actor))
                : Optional.empty();
    }
}
