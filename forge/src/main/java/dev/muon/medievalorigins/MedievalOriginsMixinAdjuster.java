package dev.muon.medievalorigins;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.List;

public class MedievalOriginsMixinAdjuster implements MixinAnnotationAdjuster {

    @Override
    public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode method, AdjustableAnnotationNode annotation) {
        if (mixinClassName.equals("io.github.apace100.apoli.mixin.integration.connector.EntityMixin")) {
            if (method.name.equals("preventPhasingSuffocation") && annotation.is(Inject.class)) {
                MedievalOrigins.LOG.info("Disabled Apoli#EntityMixin#preventPhasingSuffocation");
                return null;
            }
        }

        if (mixinClassName.equals("io.github.apace100.apoli.mixin.integration.connector.ServerPlayerInteractionManagerMixin")) {
            if ((method.name.equals("cacheBlock") || method.name.equals("origins$onBlockBreak")) && annotation.is(Inject.class)) {
                MedievalOrigins.LOG.info("Disabled Apoli#ServerPlayerInteractionManagerMixin#{}", method.name);
                return null;
            }
        }

        return annotation;
    }
}