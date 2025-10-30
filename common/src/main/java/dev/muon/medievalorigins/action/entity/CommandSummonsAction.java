package dev.muon.medievalorigins.action.entity;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ISummon;
import dev.muon.medievalorigins.entity.SummonTracker;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class CommandSummonsAction {

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("command_summons"),
                new SerializableData()
                        .add("command", SerializableDataTypes.STRING),
                CommandSummonsAction::action
        );
    }

    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;

        String command = data.getString("command");
        Collection<ISummon> summons = SummonTracker.getSummonsForOwner(living.getUUID());

        switch (command.toLowerCase()) {
            case "sit" -> {
                summons.forEach(summon -> {
                    summon.setOrderedToSit(true);
                    // Stop current navigation when ordered to sit
                    if (summon.getSelfAsMob() != null) {
                        summon.getSelfAsMob().getNavigation().stop();
                    }
                    if (entity instanceof Player player) {
                        player.displayClientMessage(Component.translatable("message.medievalorigins.summon.sit"), true);
                    }
                });
            }
            case "follow" -> {
                summons.forEach(summon -> {
                    summon.setOrderedToSit(false);
                    if (entity instanceof Player player) {
                        player.displayClientMessage(Component.translatable("message.medievalorigins.summon.follow"), true);
                    }
                });
            }
            case "come" -> {
                summons.forEach(summon -> {
                    summon.getSelfAsMob().teleportTo(entity.getX(), entity.getY(), entity.getZ());
                    if (entity instanceof Player player) {
                        player.displayClientMessage(Component.translatable("message.medievalorigins.summon.come"), true);
                    }
                });
            }
        }
    }
}

