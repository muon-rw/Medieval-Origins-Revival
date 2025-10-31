package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ModifyPierceLevelPower extends ValueModifyingPower {
    private final Consumer<Entity> selfAction;

    public ModifyPierceLevelPower(PowerType<?> type, LivingEntity entity, Consumer<Entity> selfAction) {
        super(type, entity);
        this.selfAction = selfAction;
    }

    public void executeActions() {
        if (selfAction != null) {
            selfAction.accept(this.entity);
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("modify_pierce_level"),
                new SerializableData()
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null),
                data -> (type, player) -> {
                    ModifyPierceLevelPower power = new ModifyPierceLevelPower(
                            type,
                            player,
                            data.get("self_action")
                    );
                    Objects.requireNonNull(power);
                    data.ifPresent("modifier", power::addModifier);
                    data.ifPresent("modifiers", (List<Modifier> mods) -> {
                        Objects.requireNonNull(power);
                        mods.forEach(power::addModifier);
                    });
                    return power;
                }
        ).allowCondition();
    }
}

