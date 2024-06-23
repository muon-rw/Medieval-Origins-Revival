package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class AlliedCondition {
    public static boolean condition(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        Entity actor = pair.getA();
        Entity target = pair.getB();

        return actor.isAlliedTo(target);
    }

    public static ConditionFactory<Tuple<Entity, Entity>> getFactory() {
        return new ConditionFactory<>(MedievalOrigins.loc("allied"), new SerializableData(), AlliedCondition::condition);
    }
}