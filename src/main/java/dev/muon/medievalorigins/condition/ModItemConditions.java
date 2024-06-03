package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.item.*;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantment;

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

    }

    private static void register (ConditionFactory <ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}