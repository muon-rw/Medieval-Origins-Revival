package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.enchantment.ModEnchantments;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
        register(new ConditionFactory<>(MedievalOrigins.loc("is_melee_weapon"), new SerializableData(), (data, stack) ->
                Enchantments.SHARPNESS.canEnchant(stack) || stack.getItem() instanceof DiggerItem && ((DiggerItem) stack.getItem()).getAttackDamage() > 0
        ));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_summon_weapon"), new SerializableData(), (data, stack) ->
                stack.getItem() == Items.BOW || stack.getItem() instanceof DiggerItem || stack.getItem() instanceof SwordItem
        ));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_bow"), new SerializableData(), (data, stack) ->
                stack.getItem() instanceof BowItem
        ));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_dagger"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof SwordItem || Enchantments.SHARPNESS.canEnchant(stack))
                    && (itemName.contains("dagger") || itemName.contains("knife") || itemName.contains("sai") || itemName.contains("athame"));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_valkyrie_weapon"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof SwordItem || stack.getItem() instanceof TridentItem || Enchantments.SHARPNESS.canEnchant(stack) || Enchantments.PIERCING.canEnchant(stack))
                    && (itemName.contains("glaive") || itemName.contains("spear") || itemName.contains("lance") || itemName.contains("halberd"));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_fist_weapon"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof SwordItem || Enchantments.SHARPNESS.canEnchant(stack))
                    && (itemName.contains("fist") || itemName.contains("claw") || itemName.contains("gauntlet"));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_tool"), new SerializableData(), (data, stack) ->
                stack.getItem() instanceof DiggerItem || Enchantments.BLOCK_EFFICIENCY.canEnchant(stack)
        ));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_heavy_armor"), new SerializableData(), (data, stack) -> {
            if (stack.getItem() instanceof ArmorItem armorItem) {
                int featherweightLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FEATHERWEIGHT, stack);
                return armorItem.getToughness() > 0 && featherweightLevel == 0;
            }
            return false;
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_golden_armor"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof ArmorItem && (itemName.contains("gold") || itemName.contains("gilded")));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_silver_armor"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof ArmorItem && (itemName.contains("silver") || itemName.contains("iron")));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_golden_weapon"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof SwordItem && (itemName.contains("gold") || itemName.contains("gilded")));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_golden_tool"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof DiggerItem && (itemName.contains("gold") || itemName.contains("gilded")));
        }));



        register(new ConditionFactory<>(MedievalOrigins.loc("is_axe"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return stack.getItem() instanceof AxeItem || (stack.getItem() instanceof TieredItem && itemName.contains("axe") && !itemName.contains("pickaxe"));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_silver_weapon"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof SwordItem && (itemName.contains("silver") || itemName.contains("iron")));
        }));

        register(new ConditionFactory<>(MedievalOrigins.loc("is_silver_tool"), new SerializableData(), (data, stack) -> {
            String itemName = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            return (stack.getItem() instanceof DiggerItem && (itemName.contains("silver") || itemName.contains("iron")));
        }));
    }

    private static void register (ConditionFactory <ItemStack> conditionFactory) {
        Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}