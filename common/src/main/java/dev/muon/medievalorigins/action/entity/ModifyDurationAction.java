package dev.muon.medievalorigins.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.entity.SummonedMob;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.EntityCtx;

public final class ModifyDurationAction implements ActionType<EntityCtx, ModifyDurationAction.Cfg> {

    public record Cfg(float multiplier, boolean makePermanent) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.FLOAT.optionalFieldOf("multiplier", 1.0f).forGetter(Cfg::multiplier),
            Codec.BOOL.optionalFieldOf("make_permanent", false).forGetter(Cfg::makePermanent)
    ).apply(i, Cfg::new));

    @Override
    public MapCodec<Cfg> codec() {
        return CODEC;
    }

    @Override
    public void run(Cfg cfg, EntityCtx ctx) {
        if (ctx.entity() instanceof SummonedMob summon) {
            if (cfg.makePermanent()) {
                summon.setIsLimitedLife(false);
            } else {
                summon.setLifeTicks((int) (summon.getLifeTicks() * cfg.multiplier()));
            }
        }
    }
}
