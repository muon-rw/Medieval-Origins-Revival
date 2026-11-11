package dev.muon.medievalorigins.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.action.EntityAction;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public final class ActionOnJumpPower extends PowerType<ActionOnJumpPower.Config> {

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityAction.CODEC.optionalFieldOf("entity_action").forGetter(Config::entityAction)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    public static void onJump(Entity entity) {
        PowerLookup.forEach(entity, ModPowers.ACTION_ON_JUMP, Config.class,
                cfg -> cfg.entityAction().ifPresent(action -> action.run(EntityCtx.of(entity, entity.level()))));
    }

    public record Config(Optional<EntityAction> entityAction) {
    }
}
