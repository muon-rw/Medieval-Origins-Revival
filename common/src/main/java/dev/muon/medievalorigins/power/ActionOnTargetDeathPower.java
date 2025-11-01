package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnTargetDeathPower extends CooldownPower {
    private final Consumer<Tuple<Entity, Entity>> biEntityAction;
    private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
    private final Predicate<Tuple<DamageSource, Float>> damageCondition;

    public ActionOnTargetDeathPower(PowerType<?> type, LivingEntity entity,
                                    Consumer<Tuple<Entity, Entity>> biEntityAction,
                                    Predicate<Tuple<Entity, Entity>> biEntityCondition,
                                    Predicate<Tuple<DamageSource, Float>> damageCondition,
                                    int cooldownDuration,
                                    HudRender hudRender) {
        super(type, entity, cooldownDuration, hudRender);
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.damageCondition = damageCondition;
    }

    public boolean doesApply(Entity target, DamageSource source, float amount) {
        return canUse() &&
                (damageCondition == null || damageCondition.test(new Tuple<>(source, amount))) &&
                (biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, target)));
    }

    public void executeActions(Entity target, DamageSource source, float amount) {
        if (doesApply(target, source, amount)) {
            use();
            if (biEntityAction != null) {
                biEntityAction.accept(new Tuple<>(entity, target));
            }
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("action_on_target_death"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
                data -> (type, entity) -> new ActionOnTargetDeathPower(
                        type,
                        entity,
                        data.get("bientity_action"),
                        data.get("bientity_condition"),
                        data.get("damage_condition"),
                        data.get("cooldown"),
                        data.get("hud_render")
                )
        ).allowCondition();
    }
}

