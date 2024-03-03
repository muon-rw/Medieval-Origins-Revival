package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantments;

public class ModItemConditions {
    public static void register() {
        register(new ConditionFactory<>(MedievalOrigins.loc("is_weapon"), new SerializableData(), (data, stack) ->
                Enchantments.SHARPNESS.canEnchant(stack) || stack.getItem() instanceof BowItem || stack.getItem() instanceof DiggerItem && ((DiggerItem) stack.getItem()).getAttackDamage() > 0));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_bow"), new SerializableData(), (data, stack) ->
                stack.getItem() instanceof BowItem));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_dagger"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(dagger|sai|knife)[a-z_]*");
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_fist_weapon"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(fist|claw|gauntlet)[a-z_]*");
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_tool"), new SerializableData(), (data, stack) ->
                stack.getItem() instanceof DiggerItem && Enchantments.BLOCK_EFFICIENCY.canEnchant(stack)));

        register(new ConditionFactory<>(MedievalOrigins.loc("golden_armor"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*");
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("silver_armor"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof ArmorItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(silver|iron)[a-z_]*");
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("golden_weapon"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof SwordItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*");
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("golden_tool"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof DiggerItem && BuiltInRegistries.ITEM.getKey(item).getPath().matches("[a-z_]*(gold|gilded)[a-z_]*");
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_chestplate"), new SerializableData(), (data, stack) -> {
            Item item = stack.getItem();
            return item instanceof ArmorItem && ((ArmorItem) item).getEquipmentSlot() == EquipmentSlot.CHEST;
        }));
    }

    private static void register(ConditionFactory<ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}