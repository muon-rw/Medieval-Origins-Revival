package dev.muon.medievalorigins.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cammiescorner.icarus.client.IcarusClient;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.power.IcarusWingsPowerType;
import dev.muon.medievalorigins.power.PixieWingsPowerType;
import dev.muon.medievalorigins.util.ItemDataUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(IcarusClient.class)
public abstract class IcarusClientMixin {


    // Todo: change Player->AbstractClientPlayer and Icarus to 4.5.0 if it stops breaking dev environment
    @ModifyExpressionValue(method = "onPlayerTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"))
    private static int modifyArmorModifier(int original, @Local(argsOnly = true) Player player) {
        int armorValueSum = 0;
        Iterable<ItemStack> armorSlots = player.getArmorSlots();
        for (ItemStack slottedStack : armorSlots ) {
            if (slottedStack.isEmpty() || ItemDataUtil.getEnchantmentLevel(slottedStack, ModEnchantments.FEATHERWEIGHT, player.level()) > 0) {
                continue;
            }
            if (slottedStack.getItem() instanceof ArmorItem armorItem) {
                armorValueSum += armorItem.getDefense();
            }
        }
        return armorValueSum;
    }

    @ModifyReturnValue(method = "getWingsForRendering", at = @At(value = "RETURN"))
    private static ItemStack renderOriginWings(ItemStack original, LivingEntity entity) {
        var powerHolder = PowerHolderComponent.getOptional(entity);
        if (powerHolder.isEmpty()) return original;

        if (original.isEmpty()) {
            var icarusWings = powerHolder.get().getPowerTypes(IcarusWingsPowerType.class).stream()
                    .filter(PowerType::isActive)
                    .findFirst();
            if (icarusWings.isPresent()) {
                return icarusWings.get().getWingsType();
            }
        } else {
            var pixieWings = powerHolder.get().getPowerTypes(PixieWingsPowerType.class).stream()
                    .filter(PowerType::isActive)
                    .findFirst();
            if (pixieWings.isPresent()) {
                return new ItemStack(Items.AIR);
            }
        }
        return original;
    }
}