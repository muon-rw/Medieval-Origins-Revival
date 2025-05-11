package dev.muon.medievalorigins.condition;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class LookingDownCondition extends EntityCondition<NoConfiguration> {

    public LookingDownCondition() {
        super(NoConfiguration.CODEC);
    }

    @Override
    public boolean check(NoConfiguration configuration, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            float pitch = livingEntity.getXRot();
            return pitch >= 70.0f && pitch <= 90.0f;
        }
        return false;
    }
}