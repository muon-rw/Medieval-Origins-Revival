package dev.muon.medievalorigins.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.cammiescorner.icarus.client.IcarusClient;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.power.ModPowers;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = IcarusClient.class, remap = false)
public class IcarusClientMixin {
    @ModifyVariable(method = "onPlayerTick(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(value = "STORE", opcode = Opcodes.FSTORE),
            ordinal = 0)
    private static float modifyArmorModifier(float modifier, Player player) {
        var cfg = IcarusHelper.getConfigValues(player);
        int armorValueSum = 0;
        Iterable<ItemStack> armorItems = player.getArmorSlots();
        for (ItemStack armorItem : armorItems) {
            if (armorItem.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FEATHERWEIGHT.get(), armorItem) > 0) {
                continue;
            }
            if (armorItem.getItem() instanceof ArmorItem armor) {
                armorValueSum += armor.getDefense();
            }
        }
        return cfg.armorSlows() ? Math.max(1.0F, armorValueSum / 20.0F * cfg.maxSlowedMultiplier()) : 1.0F;
    }

    @ModifyReturnValue(method = "getWingsForRendering", at = @At(value = "RETURN"))
    private static ItemStack renderOriginWings(ItemStack original, LivingEntity entity) {
        if (original.isEmpty()) {
            IPowerContainer powerContainer = ApoliAPI.getPowerContainer(entity);
            if (powerContainer != null) {
                var playerPowers = powerContainer.getPowers(ModPowers.ICARUS_WINGS.get());
                if (!playerPowers.isEmpty()) {
                    return playerPowers.get(0).value().getConfiguration().getWingsType();
                }
            }
        }
        return original;
    }
}