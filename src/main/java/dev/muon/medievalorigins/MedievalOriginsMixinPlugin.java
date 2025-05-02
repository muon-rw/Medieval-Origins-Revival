package dev.muon.medievalorigins;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MedievalOriginsMixinPlugin implements IMixinConfigPlugin {
    private static final String COMPAT_PACKAGE_PREFIX = "dev.muon.medievalorigins.mixin.compat.";

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(COMPAT_PACKAGE_PREFIX)) {
            String remainingPath = mixinClassName.substring(COMPAT_PACKAGE_PREFIX.length());
            int firstDotIndex = remainingPath.indexOf('.');
            if (firstDotIndex != -1) {
                String modId = remainingPath.substring(0, firstDotIndex);
                boolean shouldApply = FabricLoader.getInstance().isModLoaded(modId);
                if (!shouldApply) {
                    MedievalOrigins.LOGGER.info("Skipping compat mixin " + mixinClassName + " because mod '" + modId + "' is not loaded.");
                }
                return shouldApply;
            } else {
                MedievalOrigins.LOGGER.warn("Warning: Malformed compat mixin path: {}", mixinClassName);
                return false;
            }
        }

        return true;
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