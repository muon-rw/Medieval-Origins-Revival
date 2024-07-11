package dev.muon.medievalorigins.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class MermodTailPower implements IDynamicFeatureConfiguration {
    private final ResourceLocation texture;
    private final int model;
    private final int tailColor;
    private final boolean hasBra;
    private final int braColor;
    private final boolean hasGradient;
    private final int gradientColor;
    private final boolean hasGlint;
    private final boolean permanent;

    public MermodTailPower(ResourceLocation texture, int model, int tailColor, boolean hasBra, int braColor, boolean hasGradient, int gradientColor, boolean hasGlint, boolean permanent) {
        this.texture = texture;
        this.model = model;
        this.tailColor = tailColor;
        this.hasBra = hasBra;
        this.braColor = braColor;
        this.hasGradient = hasGradient;
        this.gradientColor = gradientColor;
        this.hasGlint = hasGlint;
        this.permanent = permanent;
    }

    // Getters
    public ResourceLocation getTexture() { return texture; }
    public int getModel() { return model; }
    public int getTailColor() { return tailColor; }
    public boolean hasBra() { return hasBra; }
    public int getBraColor() { return braColor; }
    public boolean hasGradient() { return hasGradient; }
    public int getGradientColor() { return gradientColor; }
    public boolean hasGlint() { return hasGlint; }
    public boolean isPermanent() { return permanent; }

    public static boolean hasPower(Entity entity) {
        IPowerContainer powerContainer = ApoliAPI.getPowerContainer(entity);
        return powerContainer != null && powerContainer.hasPower(ModPowers.MERMOD_TAIL.get());
    }

    public static MermodTailPower getTailStyle(Player player) {
        IPowerContainer powerContainer = ApoliAPI.getPowerContainer(player);
        if (powerContainer != null && ModPowers.MERMOD_TAIL != null) {
            var powers = powerContainer.getPowers(ModPowers.MERMOD_TAIL.get());
            if (!powers.isEmpty()) {
                return powers.get(0).value().getConfiguration();
            }
        }
        return null;
    }

    public static final Codec<MermodTailPower> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(power -> power.texture),
            Codec.INT.fieldOf("model").forGetter(power -> power.model),
            Codec.INT.fieldOf("tailColor").forGetter(power -> power.tailColor),
            Codec.BOOL.fieldOf("hasBra").forGetter(power -> power.hasBra),
            Codec.INT.fieldOf("braColor").forGetter(power -> power.braColor),
            Codec.BOOL.fieldOf("hasGradient").forGetter(power -> power.hasGradient),
            Codec.INT.fieldOf("gradientColor").forGetter(power -> power.gradientColor),
            Codec.BOOL.fieldOf("hasGlint").forGetter(power -> power.hasGlint),
            Codec.BOOL.fieldOf("permanent").forGetter(power -> power.permanent)
    ).apply(instance, MermodTailPower::new));
}