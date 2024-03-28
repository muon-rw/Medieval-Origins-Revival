package dev.muon.medievalorigins.mixin;

import dev.cammiescorner.icarus.client.IcarusClient;
import dev.cammiescorner.icarus.common.items.WingItem;
import dev.cammiescorner.icarus.core.integration.IcarusConfig;
import dev.cammiescorner.icarus.core.network.c2s.DeleteHungerMessage;
import dev.cammiescorner.icarus.core.util.IcarusHelper;
import dev.cammiescorner.icarus.core.util.SlowFallEntity;
import dev.emi.trinkets.api.SlotReference;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.power.WingsPower;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.item.ArmorItem;

@Mixin(value = IcarusHelper.class)
public class IcarusHelperMixin {
    @Shadow
    private static final TagKey<Item> FREE_FLIGHT;

    /**
     * @author muon-rw
     * @reason Replaces the Icarus flight speed logic to factor actual equipped armor instead of the entity's armor attribute, and factor in our Featherweight enchantment
     */
    @Overwrite
    public static void applySpeed(Player player, ItemStack stack) {
        ((SlowFallEntity)player).setSlowFalling(false);
        Vec3 rotation = player.getLookAngle();
        Vec3 velocity = player.getDeltaMovement();

        int armorValueSum = 0;
        Iterable<ItemStack> armorItems = player.getArmorSlots();
        for (ItemStack armorItem : armorItems) {
            if (armorItem.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FEATHERWEIGHT, armorItem) > 0) {
                continue;
            }
            if (armorItem.getItem() instanceof ArmorItem) {
                ArmorItem armor = (ArmorItem) armorItem.getItem();
                armorValueSum += armor.getDefense();
            }
        }

        float modifier = IcarusClient.armourSlows ? Math.max(1.0F, armorValueSum / 20.0F * IcarusClient.maxSlowedMultiplier) : 1.0F;
        float speed = IcarusClient.wingSpeed * (player.getXRot() < -75.0F && player.getXRot() > -105.0F ? 2.75F : 1.0F) / modifier;
        player.setDeltaMovement(velocity.add(rotation.x * (double)speed + (rotation.x * 1.5 - velocity.x) * (double)speed, rotation.y * (double)speed + (rotation.y * 1.5 - velocity.y) * (double)speed, rotation.z * (double)speed + (rotation.z * 1.5 - velocity.z) * (double)speed));
        if (!stack.is(FREE_FLIGHT) && !player.isCreative()) {
            DeleteHungerMessage.send();
        }
    }

    static {
        FREE_FLIGHT = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("icarus", "free_flight"));
    }
}