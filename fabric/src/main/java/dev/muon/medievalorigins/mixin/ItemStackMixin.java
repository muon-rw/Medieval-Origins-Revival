package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.muon.medievalorigins.power.EdibleItemPower;
import io.github.apace100.apoli.access.EntityLinkedItemStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemStack.class, priority = 1500)
public class ItemStackMixin {

    // Not needed for the vanilla logic chain, but just covering all the bases, since apoli already has this context we can use
    @SuppressWarnings("all")
    @ModifyReturnValue(method = "isEdible", at = @At("RETURN"))
    private boolean modifyIsEdible(boolean original) {
        ItemStack self = (ItemStack) (Object) this;
        if (self == null) return original;
        EntityLinkedItemStack heldStack = (EntityLinkedItemStack) (Object) self;
        return original || PowerHolderComponent.hasPower(heldStack.getEntity(), EdibleItemPower.class, power -> power.doesApply(heldStack.getEntity().level(), self));
    }
}
