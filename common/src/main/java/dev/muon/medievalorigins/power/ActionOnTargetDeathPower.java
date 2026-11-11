package dev.muon.medievalorigins.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.action.BiEntityAction;
import dev.overgrown.apoli.condition.BiEntityCondition;
import dev.overgrown.apoli.condition.DamageCondition;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import dev.overgrown.apoli.condition.context.DamageCtx;
import dev.overgrown.apoli.data.Expression;
import dev.overgrown.apoli.data.HudRender;
import dev.overgrown.apoli.power.PowerContainer;
import dev.overgrown.apoli.power.PowerContainerImpl;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerResources;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.OptionalDouble;

public final class ActionOnTargetDeathPower extends PowerType<ActionOnTargetDeathPower.Config> {

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiEntityAction.CODEC.optionalFieldOf("bientity_action").forGetter(Config::bientityAction),
            BiEntityCondition.CODEC.optionalFieldOf("bientity_condition").forGetter(Config::bientityCondition),
            DamageCondition.CODEC.optionalFieldOf("damage_condition").forGetter(Config::damageCondition),
            Expression.INT_OR_EXPR.optionalFieldOf("cooldown", Expression.constant(1)).forGetter(Config::cooldown),
            HudRender.CODEC.optionalFieldOf("hud_render", HudRender.DONT_RENDER).forGetter(Config::hudRender)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    @Override
    public boolean isCooldown() {
        return true;
    }

    @Override
    public OptionalDouble readResource(ResourceLocation powerId, Config cfg, PowerContainer holder) {
        return PowerResources.readDeadline(holder, powerId);
    }

    @Override
    public OptionalDouble writeResource(ResourceLocation powerId, Config cfg, PowerContainer holder, double value) {
        return PowerResources.writeDeadline(holder, powerId, value, PowerResources.cooldownTicks(cfg.cooldown(), holder));
    }

    @Override
    public OptionalDouble resourceBound(ResourceLocation powerId, Config cfg, PowerContainer holder, boolean max) {
        return OptionalDouble.of(max ? Math.max(PowerResources.cooldownTicks(cfg.cooldown(), holder), 0) : 0);
    }

    public static void onTargetDeath(LivingEntity killer, LivingEntity target, DamageSource source, float amount) {
        if (killer.level().isClientSide() || !(PowerContainer.of(killer) instanceof PowerContainerImpl container)) {
            return;
        }
        long now = killer.level().getGameTime();
        PowerLookup.forEachEntry(killer, ModPowers.ACTION_ON_TARGET_DEATH, Config.class, (powerId, cfg) -> {
            if (container.getAuxIntOr(powerId, 0) > (int) now) {
                return;
            }
            if (cfg.damageCondition().isPresent()
                    && !cfg.damageCondition().get().test(new DamageCtx(source, target, target.level(), amount))) {
                return;
            }
            BiEntityCtx ctx = new BiEntityCtx(killer, target, killer.level());
            if (cfg.bientityCondition().isPresent() && !cfg.bientityCondition().get().test(ctx)) {
                return;
            }
            container.setAuxInt(powerId, (int) now + Math.max(PowerResources.cooldownTicks(cfg.cooldown(), container), 0));
            cfg.bientityAction().ifPresent(action -> action.run(ctx));
        });
    }

    public record Config(
            Optional<BiEntityAction> bientityAction,
            Optional<BiEntityCondition> bientityCondition,
            Optional<DamageCondition> damageCondition,
            Expression cooldown,
            HudRender hudRender
    ) implements dev.overgrown.apoli.power.HudRendered {
    }
}
