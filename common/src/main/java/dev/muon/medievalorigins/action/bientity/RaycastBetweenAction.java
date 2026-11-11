package dev.muon.medievalorigins.action.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import dev.overgrown.apoli.data.ParticleEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public final class RaycastBetweenAction implements ActionType<BiEntityCtx, RaycastBetweenAction.Cfg> {

    public record Cfg(ParticleEffect particle, double spacing) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ParticleEffect.CODEC.fieldOf("particle").forGetter(Cfg::particle),
            Codec.DOUBLE.optionalFieldOf("spacing", 0.5).forGetter(Cfg::spacing)
    ).apply(i, Cfg::new));

    @Override
    public MapCodec<Cfg> codec() {
        return CODEC;
    }

    @Override
    public void run(Cfg cfg, BiEntityCtx ctx) {
        Entity actor = ctx.actor();
        Entity target = ctx.target();
        if (actor == null || target == null) {
            return;
        }

        ParticleOptions particle = cfg.particle().resolve(ctx.level(), actor);
        if (particle == null) {
            return;
        }

        createParticlesAtHitPos(actor, new EntityHitResult(target), particle, cfg.spacing());
    }

    private static void createParticlesAtHitPos(Entity entity, HitResult hitResult, ParticleOptions particle, double spacing) {
        if (entity.level().isClientSide()) return;

        double distanceTo = hitResult.distanceTo(entity);

        for (double d = spacing; d < distanceTo; d += spacing) {
            double lerpValue = Mth.clamp(d / distanceTo, 0.0, 1.0);
            ((ServerLevel) entity.level()).sendParticles(
                    particle,
                    Mth.lerp(lerpValue, entity.getEyePosition().x(), hitResult.getLocation().x()),
                    Mth.lerp(lerpValue, entity.getEyePosition().y(), hitResult.getLocation().y()),
                    Mth.lerp(lerpValue, entity.getEyePosition().z(), hitResult.getLocation().z()),
                    1, 0, 0, 0, 0
            );
        }
    }
}
