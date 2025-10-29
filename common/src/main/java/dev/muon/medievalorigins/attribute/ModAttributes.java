package dev.muon.medievalorigins.attribute;

import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {

    // Projectile damage attributes
    public static final Attribute PROJECTILE_DAMAGE_BONUS = register("projectile_damage_bonus",
            new RangedAttribute("attribute.name.medievalorigins.projectile_damage_bonus", 0.0D, 0.0D, 2048.0D)
                    .setSyncable(true));
    
    private static Attribute register(String name, Attribute attribute) {
        return Registry.register(BuiltInRegistries.ATTRIBUTE, MedievalOrigins.loc(name), attribute);
    }
    
    public static void register() {
        MedievalOrigins.LOG.info("Registering Medieval Origins attributes");
    }
}

