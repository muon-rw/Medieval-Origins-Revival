package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EdibleItemPower extends Power {
    private final Predicate<ItemStack> itemCondition;
    private final FoodProperties foodComponent;
    private final UseAnim useAction;
    private final ItemStack returnStack;
    private final SoundEvent sound;
    public final Consumer<Entity> entityActionWhenEaten;
    public final Consumer<Tuple<Level, ItemStack>> itemActionWhenEaten;

    public EdibleItemPower(PowerType<?> type, LivingEntity entity,
                           Predicate<ItemStack> itemCondition,
                           FoodProperties foodComponent,
                           UseAnim useAction,
                           ItemStack returnStack,
                           SoundEvent sound,
                           Consumer<Entity> entityActionWhenEaten,
                           Consumer<Tuple<Level, ItemStack>> itemActionWhenEaten) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.foodComponent = foodComponent;
        this.useAction = useAction;
        this.returnStack = returnStack;
        this.sound = sound;
        this.entityActionWhenEaten = entityActionWhenEaten;
        this.itemActionWhenEaten = itemActionWhenEaten;
    }

    public boolean doesApply(Level level, ItemStack stack) {
        return this.itemCondition == null || this.itemCondition.test(stack);
    }

    public FoodProperties getFoodComponent() {
        return foodComponent;
    }

    public UseAnim getUseAction() {
        return useAction;
    }

    public ItemStack getReturnStack() {
        return returnStack;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("edible_item"),
                new SerializableData()
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("food_component", SerializableDataTypes.FOOD_COMPONENT, null)
                        .add("use_action", SerializableDataType.enumValue(UseAnim.class), UseAnim.EAT)
                        .add("return_stack", SerializableDataTypes.ITEM_STACK, null)
                        .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("item_action", ApoliDataTypes.ITEM_ACTION, null),
                data -> (type, entity) -> new EdibleItemPower(
                        type,
                        entity,
                        data.get("item_condition"),
                        data.get("food_component"),
                        data.get("use_action"),
                        data.get("return_stack"),
                        data.get("sound"),
                        data.get("entity_action"),
                        data.get("item_action")
                )
        ).allowCondition();
    }
}

