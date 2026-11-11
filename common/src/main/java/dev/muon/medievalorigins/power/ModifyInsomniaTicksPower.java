package dev.muon.medievalorigins.power;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.data.AttributeModifier;
import dev.overgrown.apoli.data.AttributeModifierHelper;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ModifyInsomniaTicksPower extends PowerType<ModifyInsomniaTicksPower.Config> {

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AttributeModifier.CODEC.optionalFieldOf("modifier").forGetter(Config::modifier),
            AttributeModifier.LIST_OR_SINGLE.optionalFieldOf("modifiers").forGetter(Config::modifiers)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    public static int modify(Player player, int ticksSinceRest) {
        List<AttributeModifier> modifiers = new ArrayList<>();
        PowerLookup.forEach(player, ModPowers.MODIFY_INSOMNIA_TICKS, Config.class, cfg -> modifiers.addAll(cfg.allModifiers()));
        return modifiers.isEmpty() ? ticksSinceRest : (int) AttributeModifierHelper.apply((double) ticksSinceRest, modifiers, player);
    }

    public record Config(Optional<AttributeModifier> modifier, Optional<List<AttributeModifier>> modifiers) {
        public List<AttributeModifier> allModifiers() {
            return AttributeModifierHelper.flatten(modifier, modifiers);
        }
    }
}
