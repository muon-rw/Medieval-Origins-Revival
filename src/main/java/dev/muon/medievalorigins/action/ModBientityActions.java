package dev.muon.medievalorigins.action;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Registry;
import net.minecraft.util.Tuple;

public class ModBientityActions {
    public static void register() {
        register(TransferItemAction.getFactory());
        register(AttributedDamageAction.getFactory());
        register(SpellDamageAction.getFactory());
    }

    private static void register(ActionFactory<Tuple<Entity, Entity>> serializer) {
        Registry.register(ApoliRegistries.BIENTITY_ACTION, serializer.getSerializerId(), serializer);
    }
}