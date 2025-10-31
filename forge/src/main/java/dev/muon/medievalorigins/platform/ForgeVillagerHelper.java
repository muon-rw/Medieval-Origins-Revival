package dev.muon.medievalorigins.platform;

import dev.muon.medievalorigins.mixin.ZombieVillagerInvoker;
import dev.muon.medievalorigins.platform.services.IVillagerHelper;
import net.minecraft.world.entity.monster.ZombieVillager;

import javax.annotation.Nullable;
import java.util.UUID;

public class ForgeVillagerHelper implements IVillagerHelper {

    @Override
    public void startVillagerConversion(ZombieVillager zombieVillager, @Nullable UUID conversionStarter, int conversionTime) {
        ((ZombieVillagerInvoker) zombieVillager).callStartConverting(conversionStarter, conversionTime);
    }
}

