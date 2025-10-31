package dev.muon.medievalorigins.platform.services;

import net.minecraft.world.entity.monster.ZombieVillager;

import java.util.UUID;

/**
 * Service interface for villager-related operations that require platform-specific access.
 */
public interface IVillagerHelper {

    /**
     * Starts the conversion process for a zombie villager.
     *
     * @param zombieVillager The zombie villager to convert.
     * @param conversionStarter The UUID of the player starting the conversion (can be null).
     * @param conversionTime The time in ticks for the conversion to complete.
     */
    void startVillagerConversion(ZombieVillager zombieVillager, UUID conversionStarter, int conversionTime);
}

