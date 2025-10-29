package dev.muon.medievalorigins;

import dev.muon.medievalorigins.attribute.ModAttributes;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonedSkeleton;
import dev.muon.medievalorigins.entity.SummonedZombie;
import dev.muon.medievalorigins.entity.SummonedWitherSkeleton;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(modid = MedievalOrigins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SUMMON_SKELETON.get(), SummonedSkeleton.createAttributes().build());
        event.put(ModEntities.SUMMON_ZOMBIE.get(), SummonedZombie.createAttributes().build());
        event.put(ModEntities.SUMMON_WITHER_SKELETON.get(), SummonedWitherSkeleton.createAttributes().build());
    }

    @SubscribeEvent
    public void applyAttribs(EntityAttributeModificationEvent event) {
        event.add(ModEntities.SUMMON_SKELETON.get(), ModAttributes.PROJECTILE_DAMAGE_BONUS, 0);
        event.add(ModEntities.SUMMON_WITHER_SKELETON.get(), ModAttributes.PROJECTILE_DAMAGE_BONUS, 0);
    }

}

