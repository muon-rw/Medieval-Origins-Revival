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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemStack.class, priority = 1500)
public class ItemStackMixin {

    @Unique
    @SuppressWarnings("all")
    private static final Entity getEntityFromItemStack(ItemStack stack) {
        EntityLinkedItemStack heldStack = (EntityLinkedItemStack) (Object) stack;
        return heldStack.getEntity();
    }

    @Unique
    private ItemStack getSelf() {
        return (ItemStack) (Object) this;
    }


    // Not needed for the vanilla logic chain, but just covering all the bases, since apoli already has this context we can use
    @ModifyReturnValue(method = "isEdible", at = @At("RETURN"))
    private boolean modifyIsEdible(boolean original) {
        ItemStack self = getSelf();
        if (self == null) return original;
        return original || PowerHolderComponent.hasPower(getEntityFromItemStack(self), EdibleItemPower.class, power -> power.doesApply(getEntityFromItemStack(self).level(), self));
    }

    @ModifyVariable(method = "setDamageValue", at = @At(value = "HEAD"), argsOnly = true)
    private int medievalorigins$modifyDurabilityChange(int damage) {
        ItemStack self = getSelf();
        if (self == null) return damage;
        Entity entity = getEntityFromItemStack(self);
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
