package dev.muon.medievalorigins.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class PixieWingsPower implements IDynamicFeatureConfiguration {
    private final ItemStack wingsType;

    public PixieWingsPower(ItemStack wingsType) {
        this.wingsType = wingsType;
    }

    public ItemStack getWingsType() {
        return wingsType;
    }

    public static boolean hasPower(Entity entity) {
        IPowerContainer powerContainer = ApoliAPI.getPowerContainer(entity);
        return powerContainer != null && powerContainer.hasPower(ModPowers.PIXIE_WINGS.get());
    }

    public static final Codec<PixieWingsPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializableDataTypes.ITEM_STACK.fieldOf("wings_type").forGetter(PixieWingsPower::getWingsType)
    ).apply(instance, PixieWingsPower::new));
}