package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class ActionOnJumpPower extends Power {
    private final Consumer<Entity> entityAction;

    public ActionOnJumpPower(PowerType<?> type, LivingEntity entity, Consumer<Entity> entityAction) {
        super(type, entity);
        this.entityAction = entityAction;
    }

    public void executeAction(Entity entity) {
        if (entityAction != null) {
            entityAction.accept(entity);
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("action_on_jump"),
                new SerializableData()
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
                data -> (type, entity) -> new ActionOnJumpPower(
                        type,
                        entity,
                        data.get("entity_action")
                )
        ).allowCondition();
    }
}

