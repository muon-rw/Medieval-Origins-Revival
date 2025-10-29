package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityGorgon.class, remap = false)
public abstract class EntityGorgonMixin {

    @ModifyReturnValue(method = "isBlindfolded(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"))
    private static boolean applyMirroring(boolean original, LivingEntity attackTarget) {
        if (original) {
            return true; // Bypass expensive checks, getEnchantmentLevel iterates over the whole stack's NBT
        }
        if (attackTarget instanceof Player player) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!helmet.isEmpty()) {
                if (helmet.getEnchantmentLevel(ModEnchantments.MIRRORING) > 0) {
                    return true;
                }
            }
        }
        return original;
    }
} 