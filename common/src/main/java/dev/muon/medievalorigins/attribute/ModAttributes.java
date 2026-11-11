package dev.muon.medievalorigins.attribute;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public final class ModAttributes {
    public static final Holder<Attribute> RANGED_DAMAGE = Services.REGISTRAR.register(BuiltInRegistries.ATTRIBUTE,
            MedievalOrigins.loc("summon.ranged_damage"),
            () -> new RangedAttribute("attribute.medievalorigins.summon.ranged_damage", 0.0D, 0.0D, 2048.0D)
                    .setSyncable(true));

    private ModAttributes() {
    }

    public static void init() {
    }
}
