package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Tuple;

import java.util.function.Predicate;

public class MobsIgnorePower extends Power {
    private final Predicate<Entity> mobCondition;
    private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

    public MobsIgnorePower(PowerType<?> type, LivingEntity entity,
                           Predicate<Entity> mobCondition,
                           Predicate<Tuple<Entity, Entity>> biEntityCondition) {
        super(type, entity);
        this.mobCondition = mobCondition;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean shouldIgnore(Entity mob, Entity holder) {
        boolean mobTest = mobCondition == null || mobCondition.test(mob);
        boolean biEntityTest = biEntityCondition == null || biEntityCondition.test(new Tuple<>(holder, mob));
        return mobTest && biEntityTest;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("mobs_ignore"),
                new SerializableData()
                        .add("mob_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data -> (type, entity) -> new MobsIgnorePower(
                        type,
                        entity,
                        data.get("mob_condition"),
                        data.get("bientity_condition")
                )
        ).allowCondition();
    }
}

