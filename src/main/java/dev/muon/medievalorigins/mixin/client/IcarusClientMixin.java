package dev.muon.medievalorigins.mixin.client;

import dev.cammiescorner.icarus.client.IcarusClient;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(IcarusClient.class)
public abstract class IcarusClientMixin {

    @ModifyVariable(method = "Ldev/cammiescorner/icarus/client/IcarusClient;onPlayerTick(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(value = "STORE", opcode = Opcodes.FSTORE),
            ordinal = 0)
    private static float modifyArmorModifier(float modifier, Player player) {
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
        return IcarusClient.armorSlows ? Math.max(1.0F, armorValueSum / 20.0F * IcarusClient.maxSlowedMultiplier) : 1.0F;
    }
}