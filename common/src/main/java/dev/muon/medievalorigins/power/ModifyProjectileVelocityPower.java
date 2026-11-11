package dev.muon.medievalorigins.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.action.EntityAction;
import dev.overgrown.apoli.codec.LoggedOptionalField;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.data.AttributeModifier;
import dev.overgrown.apoli.data.AttributeModifierHelper;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ModifyProjectileVelocityPower extends PowerType<ModifyProjectileVelocityPower.Config> {

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(Config::modifier),
            AttributeModifier.LIST_OR_SINGLE.optionalFieldOf("modifiers").forGetter(Config::modifiers),
            LoggedOptionalField.of("self_action", EntityAction.CODEC).forGetter(Config::selfAction)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    public static boolean isActive(Entity entity) {
        return PowerLookup.hasActive(entity, ModPowers.MODIFY_PROJECTILE_VELOCITY);
    }

    public static double modify(Entity entity, double baseValue) {
        List<AttributeModifier> modifiers = new ArrayList<>();
        PowerLookup.forEach(entity, ModPowers.MODIFY_PROJECTILE_VELOCITY, Config.class, cfg -> modifiers.addAll(cfg.allModifiers()));
        return modifiers.isEmpty() ? baseValue : AttributeModifierHelper.apply(baseValue, modifiers, entity);
    }

    public static void runSelfActions(Entity entity) {
        PowerLookup.forEach(entity, ModPowers.MODIFY_PROJECTILE_VELOCITY, Config.class,
                cfg -> cfg.selfAction().ifPresent(action -> action.run(EntityCtx.of(entity, entity.level()))));
    }

    public record Config(
            Optional<AttributeModifier> modifier,
            Optional<List<AttributeModifier>> modifiers,
            Optional<EntityAction> selfAction
    ) {
        public List<AttributeModifier> allModifiers() {
            return AttributeModifierHelper.flatten(modifier, modifiers);
        }
    }
}
