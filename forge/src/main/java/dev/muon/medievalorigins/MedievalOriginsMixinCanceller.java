package dev.muon.medievalorigins;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import dev.muon.medievalorigins.MedievalOrigins;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;

public class MedievalOriginsMixinCanceller implements MixinCanceller {

    // Connector Extras is a required dependency for Origins, but these mixins aren't finding their targets in this dev environment
    // Simpler to cancel for now
    // Hopefully this doesn't cause any supreme mysteries down the line.

    private static final List<String> REACH_ENTITY_ATTRIBUTES_MIXINS = List.of(
        "com.jamieswhiteshirt.reachentityattributes.mixin.ScreenHandlerMixin",
        "com.jamieswhiteshirt.reachentityattributes.mixin.InventoryValidationMixin",
        "com.jamieswhiteshirt.reachentityattributes.mixin.ItemMixin"
    );
    
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!FMLLoader.isProduction() && REACH_ENTITY_ATTRIBUTES_MIXINS.contains(mixinClassName)) {
            String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
            MedievalOrigins.LOG.warn("Cancelled reach-entity-attributes {} due to incompatible mixin targets in dev environment. " +
                    "If this isn't a development environment, you shouldn't be seeing this message!", simpleName);
            return true;
        }

        return false;
    }
}
