package dev.muon.medievalorigins.power;

import com.mojang.serialization.MapCodec;
import dev.overgrown.apoli.power.PowerLookup;
import dev.overgrown.apoli.power.PowerType;
import dev.overgrown.apoli.shared.EmptyCfg;
import net.minecraft.world.entity.Entity;

public final class PixieWingsPower extends PowerType<EmptyCfg> {

    @Override
    public MapCodec<EmptyCfg> configCodec() {
        return MapCodec.unit(EmptyCfg.INSTANCE);
    }

    public static boolean isActive(Entity entity) {
        return PowerLookup.hasActive(entity, ModPowers.PIXIE_WINGS);
    }
}
