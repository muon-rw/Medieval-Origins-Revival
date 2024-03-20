package dev.muon.medievalorigins.action;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.ModEntities;
import dev.muon.medievalorigins.entity.SummonedSkeleton;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CastSpellAction {
    public static void castAction(Player player, String spellIdentifier) {
        ItemStack itemStack = player.getMainHandItem();
        ResourceLocation spellID = new ResourceLocation(spellIdentifier);
        SpellHelper.attemptCasting(player, itemStack, spellID, false);
    }
    public static void action(SerializableData.Instance data, Entity caster) {
}

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                MedievalOrigins.loc("cast_spell"),
                new SerializableData()
                        .add("duration", SerializableDataTypes.INT, null)
                        .add("tag", SerializableDataTypes.NBT, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null),
                CastSpellAction::action
        );
    }

}
