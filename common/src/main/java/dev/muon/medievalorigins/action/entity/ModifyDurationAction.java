package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ISummon;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;

public class ModifyDurationAction {

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("modify_duration"),
                new SerializableData()
                        .add("multiplier", SerializableDataTypes.FLOAT, 1.0f)
                        .add("make_permanent", SerializableDataTypes.BOOLEAN, false),
                ModifyDurationAction::action
        );
    }

    public static void action(SerializableData.Instance data, Entity entity) {
        if (entity instanceof ISummon summon) {
            boolean makePermanent = data.getBoolean("make_permanent");
            float multiplier = data.getFloat("multiplier");

            if (makePermanent) {
                summon.setIsLimitedLife(false);
            } else {
                int currentTicks = summon.getTicksLeft();
                summon.setLifeTicks((int)(currentTicks * multiplier));
            }
        }
    }
}

