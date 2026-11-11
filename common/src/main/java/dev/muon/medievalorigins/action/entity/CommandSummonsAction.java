package dev.muon.medievalorigins.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.entity.SummonTracker;
import dev.muon.medievalorigins.entity.SummonedMob;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.EntityCtx;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public final class CommandSummonsAction implements ActionType<EntityCtx, CommandSummonsAction.Cfg> {

    public record Cfg(String command) {}

    private static final MapCodec<Cfg> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(Cfg::command)
    ).apply(i, Cfg::new));

    @Override
    public MapCodec<Cfg> codec() {
        return CODEC;
    }

    @Override
    public void run(Cfg cfg, EntityCtx ctx) {
        Entity entity = ctx.entity();
        if (!(entity instanceof LivingEntity living)) return;

        Collection<SummonedMob> summons = SummonTracker.getSummonsForOwner(living.getUUID());

        switch (cfg.command().toLowerCase()) {
            case "sit" -> summons.forEach(summon -> {
                summon.setOrderedToSit(true);
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.translatable("message.medievalorigins.summon.sit"), true);
                }
            });
            case "follow" -> summons.forEach(summon -> {
                summon.setOrderedToSit(false);
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.translatable("message.medievalorigins.summon.follow"), true);
                }
            });
            case "come" -> summons.forEach(summon -> {
                summon.getSelfAsMob().teleportTo(entity.getX(), entity.getY(), entity.getZ());
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.translatable("message.medievalorigins.summon.come"), true);
                }
            });
        }
    }
}
