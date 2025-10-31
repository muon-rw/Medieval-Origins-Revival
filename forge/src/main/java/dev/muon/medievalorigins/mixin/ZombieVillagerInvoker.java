package dev.muon.medievalorigins.mixin;

import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ZombieVillager.class)
public interface ZombieVillagerInvoker {
    /**
     * Invokes the private method {@code startConverting(UUID, int)} in {@link ZombieVillager}.
     *
     * @param pConversionStarter    The UUID of the player starting the conversion.
     * @param pVillagerConversionTime The time in ticks for the conversion.
     */
    @Invoker("startConverting")
    void callStartConverting(@Nullable UUID pConversionStarter, int pVillagerConversionTime);
} 