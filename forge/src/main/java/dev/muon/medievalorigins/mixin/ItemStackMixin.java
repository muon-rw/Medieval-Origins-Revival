package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.power.EdibleItemPower;
import dev.muon.medievalorigins.power.ModifyDurabilityChangePower;
import io.github.apace100.apoli.access.EntityLinkedItemStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemStack.class, priority = 1500)
public class ItemStackMixin {

    // Not needed for the vanilla logic chain, but just covering all the bases
    @SuppressWarnings("all")
    @ModifyReturnValue(method = "isEdible", at = @At("RETURN"))
    private boolean modifyIsEdible(boolean original) {
        ItemStack self = (ItemStack) (Object) this;
        if (self == null) return original;
        EntityLinkedItemStack heldStack = (EntityLinkedItemStack) (Object) self;
        return original || PowerHolderComponent.hasPower(heldStack.getEntity(), EdibleItemPower.class, power -> power.doesApply(heldStack.getEntity().level(), self));
    }

    @ModifyVariable(method = "setDamageValue", at = @At(value = "HEAD"), argsOnly = true)
    private int medievalorigins$modifyDurabilityChange(int damage) {
        ItemStack self = (ItemStack) (Object) this;
        if (self == null) return damage;
        EntityLinkedItemStack heldStack = (EntityLinkedItemStack) (Object) self;
        Entity entity = heldStack.getEntity();
        if (entity instanceof LivingEntity living) {
            CompoundTag tag = self.getOrCreateTag();
            int previousDamage = tag.contains("Damage", Tag.TAG_INT) ? tag.getInt("Damage") : 0;
            final int originalDurabilityChange = damage - previousDamage;
            
            // Apply modifiers from powers that match the conditions
            float modifiedValue = PowerHolderComponent.modify(living, ModifyDurabilityChangePower.class, (float) originalDurabilityChange,
                    p -> p.doesApply(living.level(), self, originalDurabilityChange));
            
            // Apply post-function from the first matching power (if any)
            int finalDurabilityChange = originalDurabilityChange;
            for (ModifyDurabilityChangePower power : PowerHolderComponent.getPowers(living, ModifyDurabilityChangePower.class)) {
                if (power.doesApply(living.level(), self, originalDurabilityChange)) {
                    finalDurabilityChange = power.postFunction(modifiedValue);
                    break;
                }
            }
            
            return previousDamage + finalDurabilityChange;
        }
        return damage;
    }
}
