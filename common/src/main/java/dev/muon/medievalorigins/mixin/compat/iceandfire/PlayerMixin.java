package dev.muon.medievalorigins.mixin.compat.iceandfire;

import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.util.ItemDataUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = Player.class, remap = true)
public class PlayerMixin implements BlacklistedFromStatues {
    @Override
    public boolean canBeTurnedToStone() {
        Player player = ((Player)(Object)this);
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        if (!helmet.isEmpty() && ItemDataUtil.getEnchantmentLevel(helmet, ModEnchantments.MIRRORING, player.level()) > 0) {
            return false;
        }
        return true;
    }
}
