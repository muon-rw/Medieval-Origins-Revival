package dev.muon.medievalorigins.platform;

import dev.muon.medievalorigins.mixin.ZombieVillagerInvoker;
import dev.muon.medievalorigins.platform.services.IVillagerHelper;
import net.minecraft.world.entity.monster.ZombieVillager;

import java.util.UUID;

public class FabricVillagerHelper implements IVillagerHelper {

    @Override
    public void startVillagerConversion(ZombieVillager zombieVillager, UUID conversionStarter, int conversionTime) {
        ((ZombieVillagerInvoker) zombieVillager).callStartConverting(conversionStarter, conversionTime);
    }
}

