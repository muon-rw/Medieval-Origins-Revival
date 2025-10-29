package dev.muon.medievalorigins.platform.services;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Platform service for accessing registered entity types.
 */
public interface IEntityHelper {
    
    /**
     * Gets the EntityType for a given entity class.
     * Platform implementations handle the difference between Forge's RegistryObject.get() and Fabric's direct access.
     * 
     * @param entityClass The entity class
     * @return The registered EntityType
     */
    <T extends Entity> EntityType<T> getEntityType(Class<T> entityClass);
}

