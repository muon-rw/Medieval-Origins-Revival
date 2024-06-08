package dev.muon.medievalorigins.configuration;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record FixedSummonTypeConfiguration(Optional<Integer> duration, @Nullable CompoundTag tag,
                                           Holder<ConfiguredEntityAction<?, ?>> action,
                                           Optional<ResourceLocation> weapon) implements IDynamicFeatureConfiguration {

    public static final Codec<FixedSummonTypeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CalioCodecHelper.optionalField(SerializableDataTypes.INT, "duration").forGetter(FixedSummonTypeConfiguration::duration),
            CalioCodecHelper.optionalField(SerializableDataTypes.NBT, "tag").forGetter(x -> Optional.ofNullable(x.tag())),
            ConfiguredEntityAction.optional("entity_action").forGetter(FixedSummonTypeConfiguration::action),
            CalioCodecHelper.optionalField(SerializableDataTypes.IDENTIFIER, "weapon").forGetter(FixedSummonTypeConfiguration::weapon)
    ).apply(instance, (t1, t2, t3, t4) -> new FixedSummonTypeConfiguration(t1, t2.orElse(null), t3, t4)));



    @Override
    public List<String> getErrors(@NotNull ICalioDynamicRegistryManager server) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        if (this.action().isBound())
            builder.addAll(this.action().value().getErrors(server).stream().map("SpawnEntity/%s"::formatted).toList());
        return builder.build();
    }

    @Override
    public List<String> getWarnings(@NotNull ICalioDynamicRegistryManager server) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        if (this.action().isBound())
            builder.addAll(this.action().value().getWarnings(server).stream().map("SpawnEntity/%s"::formatted).toList());
        return builder.build();
    }

    public @Nullable CompoundTag tag() {
        return this.tag;
    }

    public Holder<ConfiguredEntityAction<?, ?>> action() {
        return this.action;
    }
    public Optional<ResourceLocation> weapon() {
        return this.weapon;
    }
}