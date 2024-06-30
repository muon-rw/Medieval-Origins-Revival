package dev.muon.medievalorigins.condition;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityCondition;

import java.util.Objects;
import java.util.function.BiPredicate;

import net.minecraft.world.entity.Entity;

public class IsAlliedCondition extends BiEntityCondition<NoConfiguration> {
    private final BiPredicate<Entity, Entity> predicate;

    public static boolean isAllied(Entity actor, Entity target) {
        return actor.isAlliedTo(target);
    }

    public IsAlliedCondition(BiPredicate<Entity, Entity> predicate) {
        super(NoConfiguration.CODEC);
        this.predicate = predicate;
    }

    protected boolean check(NoConfiguration configuration, Entity actor, Entity target) {
        return this.predicate.test(actor, target);
    }
}
