package dev.muon.medievalorigins;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MedievalOriginsMixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, String> MOD_CHECK_CLASSES = new HashMap<>();
    
    static {
        MOD_CHECK_CLASSES.put("icarus", "dev.cammiescorner.icarus.Icarus");
    }
    
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }


    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("mixin.compat.")) {
            String[] parts = mixinClassName.split("mixin\\.compat\\.");
            if (parts.length > 1) {
                String modId = parts[1].split("\\.")[0];
                return isModLoaded(modId);
            }
        }
        return true;
    }
    
    /**
     * Check if a mod is loaded by attempting to load a class from it.
     * This works during mixin plugin initialization when the game isn't bootstrapped yet.
     */
    private boolean isModLoaded(String modId) {
        String checkClass = MOD_CHECK_CLASSES.get(modId);
        if (checkClass == null) {
            MedievalOrigins.LOG.warn("No check class defined for mod '{}', assuming not loaded", modId);
            return false;
        }
        
        try {
            Class.forName(checkClass, false, this.getClass().getClassLoader());
            MedievalOrigins.LOG.info("Detected mod '{}', enabling compatibility mixins", modId);
            return true;
        } catch (ClassNotFoundException e) {
            MedievalOrigins.LOG.debug("Mod '{}' not found, skipping compatibility mixins", modId);
            return false;
        }
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