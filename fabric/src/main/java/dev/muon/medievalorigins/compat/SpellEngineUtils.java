package dev.muon.medievalorigins.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.utils.TargetHelper;

import java.util.List;

public class SpellEngineUtils {
    // yes, this really is multiplayer safe
    // probably
    private static final ThreadLocal<Boolean> requireAmmo = ThreadLocal.withInitial(() -> true);

    public static boolean requiresAmmo() {
        return requireAmmo.get();
    }

    public static void setRequireAmmo(boolean value) {
        requireAmmo.set(value);
    }

    private static final ThreadLocal<Boolean> bypassesCooldown = ThreadLocal.withInitial(() -> false);

    public static boolean bypassesCooldown() {
        return bypassesCooldown.get();
    }

    public static void setBypassesCooldown(boolean value) {
        bypassesCooldown.set(value);
    }

    /**
     * Casts a spell using Spell Engine with automatic targeting based on target type.
     * 
     * @param entity The entity casting the spell (must be a Player)
     * @param spellId The spell resource location
     * @param requireAmmo Whether to require ammo for the cast
     * @param targetType The targeting type: "area", "raycast", or "raycast_multiple"
     * @param range The range for targeting
     */
    public static void castSpell(Entity entity, ResourceLocation spellId, boolean requireAmmo, String targetType, float range) {
        if (entity.level().isClientSide || !(entity instanceof Player player)) {
            return;
        }

        ItemStack itemStack = player.getMainHandItem();

        setBypassesCooldown(true);
        SpellCast.Attempt attempt = SpellHelper.attemptCasting(player, itemStack, spellId, requireAmmo);
        if (!attempt.isSuccess()) {
            return;
        }

        setRequireAmmo(requireAmmo);
        try {
            if (entity instanceof ServerPlayer) {
                List<Entity> targets = switch (targetType) {
                    case "area" ->
                            TargetHelper.targetsFromArea(player, range, new Spell.Release.Target.Area(), e -> e instanceof LivingEntity);
                    case "raycast" -> {
                        Entity target = TargetHelper.targetFromRaycast(player, range, e -> e instanceof LivingEntity);
                        yield target != null ? List.of(target) : List.of();
                    }
                    case "raycast_multiple" ->
                            TargetHelper.targetsFromRaycast(player, range, e -> e instanceof LivingEntity);
                    default -> List.of();
                };

                if (!targets.isEmpty()) {
                    SpellHelper.performSpell(player.level(), player, spellId, targets, SpellCast.Action.RELEASE, 1.0f);
                }
            }
        } finally {
            setRequireAmmo(true);
        }
    }

    /**
     * Casts a spell using Spell Engine with a specific target entity.
     * 
     * @param actor The entity casting the spell (must be a Player)
     * @param target The target entity
     * @param spellId The spell resource location
     * @param requireAmmo Whether to require ammo for the cast
     */
    public static void castSpellOnTarget(Entity actor, Entity target, ResourceLocation spellId, boolean requireAmmo) {
        if (!(actor instanceof Player player) || target == null || actor.level().isClientSide || target.level().isClientSide) {
            return;
        }

        ItemStack itemStack = player.getMainHandItem();

        setBypassesCooldown(true);
        SpellCast.Attempt attempt = SpellHelper.attemptCasting(player, itemStack, spellId, requireAmmo);
        if (!attempt.isSuccess()) {
            return;
        }

        setRequireAmmo(requireAmmo);
        try {
            if (actor instanceof ServerPlayer) {
                List<Entity> targets = List.of(target);
                SpellHelper.performSpell(player.level(), player, spellId, targets, SpellCast.Action.RELEASE, 1.0f);
            }
        } finally {
            setRequireAmmo(true);
        }
    }
}
