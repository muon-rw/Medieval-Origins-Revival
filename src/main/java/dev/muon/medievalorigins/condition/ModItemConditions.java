package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class ModItemConditions {
    public static void register() {

        register(new ConditionFactory<>(MedievalOrigins.loc("is_cursed"), new SerializableData(), (data, stack) -> {
            for (Enchantment enchantment : stack.getEnchantmentTags().stream().map(tag -> Enchantment.byId(tag.getId())).toList()) {
                if (enchantment != null && enchantment.isCurse()) {
                    return true;
                }
            }
            return false;
        }));

        // Switched to AutoTag for performance
        /*
        register(new ConditionFactory<>(MedievalOrigins.loc("is_weapon"), new SerializableData(), (data, stack) ->
                Enchantments.SHARPNESS.canEnchant(stack) || stack.getItem() instanceof BowItem || stack.getItem() instanceof DiggerItem && ((DiggerItem) stack.getItem()).getAttackDamage() > 0));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_bow"), new SerializableData(), (data, stack) ->
                stack.getItem() instanceof BowItem));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_dagger"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(dagger|sai|knife)[a-z_]*");
        }));*/

    }

    private static void register (ConditionFactory <ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}