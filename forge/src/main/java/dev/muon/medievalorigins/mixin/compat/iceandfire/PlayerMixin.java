package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements IBlacklistedFromStatues {
    @Override
    public boolean canBeTurnedToStone() {
        Player player = ((Player)(Object)this);
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        if (!helmet.isEmpty()) {
            for (Enchantment enchantment : helmet.getEnchantmentTags().stream().map(tag -> Enchantment.byId(tag.getId())).toList()) {
                if (enchantment == ModEnchantments.MIRRORING.get()) {
                    return false;
                }
            }
        }
        return true;
    }
}
