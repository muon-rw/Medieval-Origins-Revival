package dev.muon.medievalorigins.mixin.compat.icarus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cammiescorner.icarus.client.IcarusClient;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import dev.muon.medievalorigins.power.FaeWingsPowerType;
import dev.muon.medievalorigins.power.IcarusWingsPowerType;
import dev.muon.medievalorigins.power.PixieWingsPowerType;
import dev.muon.medievalorigins.util.ItemDataUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IcarusClient.class)
public abstract class IcarusClientMixin {

    @ModifyExpressionValue(method = "onPlayerTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;getArmorValue()I"))
    private static int modifyArmorModifier(int original, @Local(argsOnly = true) AbstractClientPlayer player) {
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
        if (original.isEmpty()) {
            List<IcarusWingsPowerType> icarusWings = PowerHolderComponent.getPowerTypes(entity, IcarusWingsPowerType.class);
            if (!icarusWings.isEmpty()) {
                return icarusWings.getFirst().getWingsType();
            }
        } else {
            boolean hasPixieWings = PowerHolderComponent.hasPowerType(entity, PixieWingsPowerType.class);
            boolean hasFaeWings = PowerHolderComponent.hasPowerType(entity, FaeWingsPowerType.class);

            if (hasPixieWings || hasFaeWings) {
                return new ItemStack(Items.AIR);
            }
        }
        return original;
    }
}