package dev.muon.medievalorigins.mixin;

import dev.cammiescorner.icarus.common.items.WingItem;
import dev.cammiescorner.icarus.core.integration.IcarusConfig;
import dev.cammiescorner.icarus.core.util.IcarusHelper;
import dev.cammiescorner.icarus.core.util.SlowFallEntity;
import dev.emi.trinkets.api.SlotReference;
import dev.muon.medievalorigins.power.WingsPower;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = WingItem.class)
public abstract class WingItemMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getFoodLevel()I"))
    private int redirectGetFoodLevel(FoodData foodData, ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof Player player && WingsPower.hasWingsPower(player)) {
            return 7;
        }
        return foodData.getFoodLevel();
    }
}