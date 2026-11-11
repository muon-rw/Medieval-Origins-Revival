package dev.muon.medievalorigins.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public final class CustomDeathSoundPower extends PowerType<CustomDeathSoundPower.Config> {

    // Built from the id instead of a registry lookup so sounds from resource packs work without registration
    private static final Codec<SoundEvent> SOUND_CODEC = ResourceLocation.CODEC.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation);

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SOUND_CODEC.fieldOf("sound").forGetter(Config::sound),
            Codec.BOOL.optionalFieldOf("muted", false).forGetter(Config::muted),
            Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(Config::volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(Config::pitch)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    /** Returns whether any active power replaced the vanilla death sound. */
    public static boolean replaceDeathSound(LivingEntity entity) {
        List<Config> powers = PowerLookup.active(entity, ModPowers.CUSTOM_DEATH_SOUND, Config.class);
        if (powers.isEmpty()) {
            return false;
        }
        if (powers.stream().noneMatch(Config::muted)) {
            powers.forEach(cfg -> cfg.play(entity));
        }
        return true;
    }

    public record Config(SoundEvent sound, boolean muted, float volume, float pitch) {
        void play(LivingEntity entity) {
            float randomizedPitch = (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + pitch;
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundSource(), volume, randomizedPitch);
        }
    }
}
