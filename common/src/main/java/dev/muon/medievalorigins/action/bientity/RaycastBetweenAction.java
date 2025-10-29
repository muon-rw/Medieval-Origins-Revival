package dev.muon.medievalorigins.action.bientity;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RaycastBetweenAction {

    public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("raycast_between"),
                new SerializableData()
                        .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                        .add("spacing", SerializableDataTypes.DOUBLE, 0.5),
                RaycastBetweenAction::action
        );
    }

    public static void action(SerializableData.Instance data, Tuple<Entity, Entity> entities) {
        Entity actor = entities.getA();
        Entity target = entities.getB();

        if (actor == null || target == null || actor.level().isClientSide()) {
            return;
        }

        ParticleOptions particle = data.get("particle");
        double spacing = data.getDouble("spacing");

        createParticlesAtHitPos(actor, new EntityHitResult(target), particle, spacing);
    }

    protected static Vec3 createDirectionVector(Vec3 pos1, Vec3 pos2) {
        return new Vec3(pos2.x() - pos1.x(), pos2.y() - pos1.y(), pos2.z() - pos1.z()).normalize();
    }

    protected static void createParticlesAtHitPos(Entity entity, HitResult hitResult, ParticleOptions particle, double spacing) {
        if (entity.level().isClientSide()) return;

        double distanceTo = hitResult.distanceTo(entity);

        for (double d = spacing; d < distanceTo; d += spacing) {
            double lerpValue = Mth.clamp(d / distanceTo, 0.0, 1.0);
            ((ServerLevel)entity.level()).sendParticles(
                    particle,
                    Mth.lerp(lerpValue, entity.getEyePosition().x(), hitResult.getLocation().x()),
                    Mth.lerp(lerpValue, entity.getEyePosition().y(), hitResult.getLocation().y()),
                    Mth.lerp(lerpValue, entity.getEyePosition().z(), hitResult.getLocation().z()),
                    1, 0, 0, 0, 0
            );
        }
    }
}

