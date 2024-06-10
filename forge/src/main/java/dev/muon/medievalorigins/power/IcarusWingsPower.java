package dev.muon.medievalorigins.power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.world.item.ItemStack;

public final class IcarusWingsPower implements IDynamicFeatureConfiguration {
    private final ItemStack wingsType;

    public IcarusWingsPower(ItemStack wingsType) {
        this.wingsType = wingsType;
    }

    public ItemStack getWingsType() {
        return wingsType;
    }

    public static final Codec<IcarusWingsPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializableDataTypes.ITEM_STACK.fieldOf("wings_type").forGetter(IcarusWingsPower::getWingsType)
    ).apply(instance, IcarusWingsPower::new));
}