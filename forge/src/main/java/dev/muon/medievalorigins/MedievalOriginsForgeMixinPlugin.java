package dev.muon.medievalorigins;

import com.bawnorton.mixinsquared.canceller.MixinCancellerRegistrar;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.loading.LoadingModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MedievalOriginsForgeMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("Medieval Origins");

    @Override
    public void onLoad(String mixinPackage) {
        LOGGER.info("MedievalOriginsForgeMixinPlugin.onLoad called with package: {}", mixinPackage);
        MixinCancellerRegistrar.register(new MedievalOriginsMixinCanceller());
        LOGGER.info("MedievalOriginsForgeMixinPlugin initialized successfully");
    }
    @Override
    public String getRefMapperConfig() {
        return null;
    }


    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        LOGGER.info("shouldApplyMixin called - target: {}, mixin: {}", targetClassName, mixinClassName);
        
        if (mixinClassName.contains(".compat.")) {

            // Each subdirectory within /compat/ is a modid
            String[] parts = mixinClassName.split("\\.");
            List<String> requiredMods = new ArrayList<>();

            // EXCEPT: Directory names that shouldn't be considered mod IDs
            Set<String> excludedDirectories = Set.of("client", "accessor");

            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("compat")) {
                    // Collect all mod IDs in the path after "compat" until the class name
                    for (int j = i + 1; j < parts.length - 1; j++) { // -1 to exclude the class name
                        String potentialModId = parts[j];
                        // Skip excluded directories
                        if (!excludedDirectories.contains(potentialModId)) {
                            requiredMods.add(potentialModId);
                        }
                    }
                    break;
                }
            }

            for (String modId : requiredMods) {
                if (!isModLoaded(modId)) {
                    LOGGER.info("Disabling mixin {} because required mod '{}' is not loaded",
                            getSimpleMixinName(mixinClassName), modId);
                    return false;
                }
            }
            if (!requiredMods.isEmpty()) {
                LOGGER.info("Enabling mixin {} - all required mods {} are loaded",
                        getSimpleMixinName(mixinClassName), requiredMods);
            }
            return true;
        }

        LOGGER.info("Enabling mixin {} (not a compat mixin)", getSimpleMixinName(mixinClassName));
        return true;
    }
    private String getSimpleMixinName(String mixinClassName) {
        // Extract just the class name from the full package path
        String[] parts = mixinClassName.split("\\.");
        return parts[parts.length - 1];
    }

    private static boolean isModLoaded(String modId) {
        return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
    }


    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}