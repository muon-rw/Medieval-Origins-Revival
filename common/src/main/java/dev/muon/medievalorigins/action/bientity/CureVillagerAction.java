package dev.muon.medievalorigins.action.bientity;

import com.mojang.serialization.MapCodec;
import dev.muon.medievalorigins.mixin.ZombieVillagerInvoker;
import dev.overgrown.apoli.action.ActionType;
import dev.overgrown.apoli.condition.context.BiEntityCtx;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;

public final class CureVillagerAction implements ActionType<BiEntityCtx, EmptyCfg> {

    @Override
    public MapCodec<EmptyCfg> codec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    @Override
    public void run(EmptyCfg cfg, BiEntityCtx ctx) {
        if (ctx.actor() instanceof Player player && ctx.target() instanceof ZombieVillager zombieVillager
                && !zombieVillager.level().isClientSide()) {
            ((ZombieVillagerInvoker) zombieVillager).callStartConverting(player.getUUID(), 1);
        }
    }
}
