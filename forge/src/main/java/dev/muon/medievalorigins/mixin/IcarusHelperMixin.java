package dev.muon.medievalorigins.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.cammiescorner.icarus.util.IcarusHelper;
import dev.muon.medievalorigins.power.ModPowers;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(value = IcarusHelper.class, remap = false)
public class IcarusHelperMixin {
    @WrapOperation(method = "hasWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean hasWingsFromOrigin(Predicate<LivingEntity> instance, Object entity, Operation<Boolean> original) {
        IPowerContainer PlayerPowers = ApoliAPI.getPowerContainer((LivingEntity) entity);
        if (PlayerPowers == null) {
            return original.call(instance, entity);
        }
        if (PlayerPowers.hasPower(ModPowers.ICARUS_WINGS.get())) {
            return true;
        }
        return original.call(instance, entity);
    }

    @WrapOperation(method = "getEquippedWings", at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object getOriginWings(Function<LivingEntity, ItemStack> instance, Object entity, Operation<ItemStack> original) {
        IPowerContainer PlayerPowers = ApoliAPI.getPowerContainer((LivingEntity) entity);
        if (PlayerPowers == null) {
            return original.call(instance, entity);
        }
        if (PlayerPowers.hasPower(ModPowers.ICARUS_WINGS.get())) {
            return null;
        }
        return original.call(instance, entity);
    }
}