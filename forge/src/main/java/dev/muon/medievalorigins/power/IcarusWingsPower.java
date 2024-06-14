package dev.muon.medievalorigins.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class IcarusWingsPower implements IDynamicFeatureConfiguration {
    private final ItemStack wingsType;

    public IcarusWingsPower(ItemStack wingsType) {
        this.wingsType = wingsType;
    }

    public ItemStack getWingsType() {
        return wingsType;
    }

    public static boolean hasPower(Entity entity) {
        IPowerContainer powerContainer = ApoliAPI.getPowerContainer(entity);
        return powerContainer != null && powerContainer.hasPower(ModPowers.ICARUS_WINGS.get());
    }

    public static ItemStack getWingsType(LivingEntity entity) {
        IPowerContainer powerContainer = ApoliAPI.getPowerContainer(entity);
        if (powerContainer != null) {
            var playerPowers = powerContainer.getPowers(ModPowers.ICARUS_WINGS.get());
            if (!playerPowers.isEmpty()) {
                return playerPowers.get(0).value().getConfiguration().getWingsType();
            }
        }
        return ItemStack.EMPTY;
    }

    public static final Codec<IcarusWingsPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializableDataTypes.ITEM_STACK.fieldOf("wings_type").forGetter(IcarusWingsPower::getWingsType)
    ).apply(instance, IcarusWingsPower::new));
}