package dev.muon.medievalorigins.condition.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public class EntityInRadiusCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Predicate<Entity> entityCondition = data.get("condition");
        double radius = data.getDouble("radius");
        int compareTo = data.getInt("compare_to");
        Comparison comparison = data.get("comparison");

        int stopAt = -1;
        switch (comparison) {
            case EQUAL:
            case LESS_THAN_OR_EQUAL:
            case GREATER_THAN:
                stopAt = compareTo + 1;
                break;
            case LESS_THAN:
            case GREATER_THAN_OR_EQUAL:
                stopAt = compareTo;
                break;
            default:
                stopAt = -1;
                break;
        }

        int count = 0;
        for (Entity target : entity.level().getEntities(entity, entity.getBoundingBox().inflate(radius))) {
            if (target != null && (entityCondition == null || entityCondition.test(target))) {
                count++;
                if (count == stopAt && stopAt != -1) {
                    break;
                }
            }
        }

        return comparison.compare(count, compareTo);
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                MedievalOrigins.loc("entity_in_radius"),
                new SerializableData()
                        .add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("radius", SerializableDataTypes.DOUBLE)
                        .add("compare_to", SerializableDataTypes.INT, 1)
                        .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                EntityInRadiusCondition::condition
        );
    }
}

