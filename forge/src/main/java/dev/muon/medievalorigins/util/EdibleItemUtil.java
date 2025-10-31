package dev.muon.medievalorigins.util;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.power.EdibleItemPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class EdibleItemUtil {
    
    /**
     * Check if any EdibleItemPower applies to the given stack and entity.
     * Used by coremod to determine if custom food properties should be used.
     */
    public static boolean doEdibleItemPowersApply(ItemStack stack, @Nullable LivingEntity entity) {
        
        if (entity == null) {
            return false;
        }

        return PowerHolderComponent.hasPower(
            entity,
            EdibleItemPower.class,
            power -> power.doesApply(entity.level(), stack)
        );
    }
    
    /**
     * Get the food properties from an EdibleItemPower if one applies.
     * Returns null if no power applies or if the power has no food component.
     * Used by coremod to inject custom food properties.
     */
    @Nullable
    public static FoodProperties getEdibleItemPowerFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        
        if (entity == null) {
            return null;
        }
        
        for (EdibleItemPower power : PowerHolderComponent.getPowers(entity, EdibleItemPower.class)) {
            if (power.doesApply(entity.level(), stack)) {
                return power.getFoodComponent();
            }
        }

        return null;
    }
}

