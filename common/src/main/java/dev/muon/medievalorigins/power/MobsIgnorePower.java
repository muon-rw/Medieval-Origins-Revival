package dev.muon.medievalorigins.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.condition.BiEntityCondition;
import dev.overgrown.apoli.condition.EntityCondition;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class MobsIgnorePower extends PowerType<MobsIgnorePower.Config> {

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityCondition.CODEC.optionalFieldOf("mob_condition").forGetter(Config::mobCondition),
            BiEntityCondition.CODEC.optionalFieldOf("bientity_condition").forGetter(Config::bientityCondition)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    public static boolean isIgnoredBy(Player player, LivingEntity mob) {
        if (mob == null || player.level().isClientSide()) {
            return false;
        }
        return PowerLookup.anyActive(player, ModPowers.MOBS_IGNORE, Config.class, cfg -> cfg.matches(mob, player));
    }

    public record Config(Optional<EntityCondition> mobCondition, Optional<BiEntityCondition> bientityCondition) {
        boolean matches(LivingEntity mob, Player player) {
            return mobCondition.map(condition -> condition.test(EntityCtx.of(mob, mob.level()))).orElse(true)
                    && bientityCondition.map(condition -> condition.test(new BiEntityCtx(player, mob, player.level()))).orElse(true);
        }
    }
}
