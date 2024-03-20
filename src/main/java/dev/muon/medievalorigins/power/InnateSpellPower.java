package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.BurnPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.api.spell.SpellContainer;
import net.spell_engine.internals.SpellContainerHelper;

import java.util.ArrayList;


public class InnateSpellPower extends Power {
    public InnateSpellPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
        addSpellToEntity(entity);
    }

    private void addSpellToEntity(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return; // Only players can have spell containers in this context
        }
        Player player = (Player) entity;
        ItemStack itemStack = player.getMainHandItem(); // Assuming the spell container is linked to the item in the main hand

        // Check if the itemStack has a valid spell container, if not create a new one
        SpellContainer container = SpellContainerHelper.containerFromItemStack(itemStack);
        if (container == null) {
            container = new SpellContainer(false, "default_pool", 10, new ArrayList<>()); // Example values for a new container
        }

        // Add the "wizards:fireball" spell to the container
        ResourceLocation fireballSpellId = new ResourceLocation("wizards", "fireball");
        SpellContainer updatedContainer = SpellContainerHelper.addSpell(fireballSpellId, container);

        // Update the itemStack with the new or modified spell container
        SpellContainerHelper.addContainerToItemStack(updatedContainer, itemStack);
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                MedievalOrigins.loc("innate_spell"),
                new SerializableData(),
                data -> (powerType, livingEntity) -> new InnateSpellPower(powerType, livingEntity)
        ).allowCondition();
    }
}