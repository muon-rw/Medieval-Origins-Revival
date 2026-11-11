package dev.muon.medievalorigins.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.data.AttributeModifier;
import dev.overgrown.apoli.data.AttributeModifierHelper;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ModifyReputationPower extends PowerType<ModifyReputationPower.Config> {

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(Config::modifier),
            AttributeModifier.LIST_OR_SINGLE.optionalFieldOf("modifiers").forGetter(Config::modifiers)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    public static int modify(Entity entity, int reputation) {
        List<AttributeModifier> modifiers = new ArrayList<>();
        PowerLookup.forEach(entity, ModPowers.MODIFY_REPUTATION, Config.class, cfg -> modifiers.addAll(cfg.allModifiers()));
        return modifiers.isEmpty() ? reputation : (int) AttributeModifierHelper.apply((double) reputation, modifiers, entity);
    }

    public record Config(Optional<AttributeModifier> modifier, Optional<List<AttributeModifier>> modifiers) {
        public List<AttributeModifier> allModifiers() {
            return AttributeModifierHelper.flatten(modifier, modifiers);
        }
    }
}
