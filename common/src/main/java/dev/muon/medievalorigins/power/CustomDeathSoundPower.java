package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CustomDeathSoundPower extends Power {
    private final SoundEvent sound;
    private final boolean muted;
    private final float volume;
    private final float pitch;

    public CustomDeathSoundPower(PowerType<?> type, LivingEntity entity,
                                 SoundEvent sound,
                                 boolean muted,
                                 float volume,
                                 float pitch) {
        super(type, entity);
        this.sound = sound;
        this.muted = muted;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void playDeathSound(Entity entity) {
        if (muted || sound == null) return;

        RandomSource random = entity instanceof LivingEntity living
                ? living.getRandom()
                : entity.level().random;

        float randomPitch = (random.nextFloat() - random.nextFloat()) * 0.2F + pitch;

        // One of these works. I'll figure out which at some point
        if (entity instanceof LivingEntity living) {
            living.playSound(sound, volume, randomPitch);
        }

        entity.level().playSound(
                null,
                entity.getX(), entity.getY(), entity.getZ(),
                sound,
                entity.getSoundSource(),
                volume,
                randomPitch
        );

        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    entity.getX(), entity.getY(), entity.getZ(),
                    sound,
                    entity.getSoundSource(),
                    volume,
                    randomPitch
            );
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("custom_death_sound"),
                new SerializableData()
                        .add("sound", SerializableDataTypes.SOUND_EVENT)
                        .add("muted", SerializableDataTypes.BOOLEAN, false)
                        .add("volume", SerializableDataTypes.FLOAT, 1.0f)
                        .add("pitch", SerializableDataTypes.FLOAT, 1.0f),
                data -> (type, entity) -> new CustomDeathSoundPower(
                        type,
                        entity,
                        data.get("sound"),
                        data.get("muted"),
                        data.get("volume"),
                        data.get("pitch")
                )
        ).allowCondition();
    }
}

