package dev.muon.medievalorigins.compat;

import dev.muon.medievalorigins.MedievalOrigins;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.network.ClientboundUpdateCastingState;
import io.redspace.ironsspellbooks.network.spell.ClientboundOnCastStarted;
import io.redspace.ironsspellbooks.network.spell.ClientboundOnClientCast;
import io.redspace.ironsspellbooks.network.spell.ClientboundSyncTargetingData;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class IronsSpellbooksUtils {

    // Continuous casting tracking for mana drain over time
    private static final Map<UUID, ContinuousCastData> CONTINUOUS_CASTS = new HashMap<>();

    // Reflection cache for PartEntity handling
    private static Class<?> partEntityClass = null;
    private static Method getParentMethod = null;
    private static boolean partEntityCheckAttempted = false;

    private static class ContinuousCastData {
        final int manaCost;
        final int costInterval;
        int ticksElapsed;

        ContinuousCastData(int manaCost, int costInterval, int ticksElapsed) {
            this.manaCost = manaCost;
            this.costInterval = costInterval;
            this.ticksElapsed = ticksElapsed;
        }
    }

    private static Entity getParentFromPartEntity(Entity entity) {
        if (!partEntityCheckAttempted) {
            partEntityCheckAttempted = true;
            try {
                partEntityClass = Class.forName("net.minecraftforge.entity.PartEntity");
                getParentMethod = partEntityClass.getMethod("getParent");
            } catch (ClassNotFoundException e) {
                MedievalOrigins.LOG.warn("PartEntity not found", e);
            } catch (NoSuchMethodException e) {
                MedievalOrigins.LOG.warn("PartEntity found but getParent method not found", e);
            }
        }
        if (partEntityClass != null && getParentMethod != null) {
            try {
                if (partEntityClass.isInstance(entity)) {
                    Object parent = getParentMethod.invoke(entity);
                    if (parent instanceof Entity) {
                        return (Entity) parent;
                    }
                }
            } catch (Exception e) {
                MedievalOrigins.LOG.error("Error accessing PartEntity parent via reflection", e);
            }
        }

        return null;
    }

    /**
     * Casts a spell using Iron's Spellbooks without explicit targeting.
     * Iron's Spellbooks will apply its default targeting behavior automatically.
     */
    public static void castSpell(Entity entity, ResourceLocation spellId, int powerLevel, Optional<Integer> castTime, 
                                  Optional<Integer> manaCost, boolean continuousCost, int costInterval) {
        if (entity.level().isClientSide() || !(entity instanceof LivingEntity caster)) {
            return;
        }

        ResourceLocation spellResourceLocation = spellId;
        // No one should be using the minecraft namespace anyway, and this is simpler
        if ("minecraft".equals(spellResourceLocation.getNamespace())) {
            spellResourceLocation = new ResourceLocation("irons_spellbooks", spellResourceLocation.getPath());
        }

        AbstractSpell spell = SpellRegistry.getSpell(spellResourceLocation);
        if (spell == null || "none".equals(spell.getSpellName())) {
            MedievalOrigins.LOG.info("No valid spell found for resource location " + spellResourceLocation);
            return;
        }

        // Cast without merging targeting data - let Iron's Spellbooks handle targeting automatically
        castSpell(caster, spell, powerLevel, null, castTime, manaCost, continuousCost, costInterval);
    }

    /**
     * Casts a spell using Iron's Spellbooks with a specific target entity (for bientity actions).
     */
    public static void castSpellOnTarget(Entity actor, Entity target, ResourceLocation spellId, int powerLevel,
                                         Optional<Integer> castTime, Optional<Integer> manaCost, 
                                         boolean continuousCost, int costInterval) {
        if (!(actor instanceof LivingEntity caster) || !(target instanceof LivingEntity livingTarget)) {
            return;
        }
        
        if (actor.level().isClientSide() || target.level().isClientSide()) {
            return;
        }
        
        ResourceLocation spellResourceLocation = spellId;
        // No one should be using the minecraft namespace anyway, and this is simpler
        if ("minecraft".equals(spellResourceLocation.getNamespace())) {
            spellResourceLocation = new ResourceLocation("irons_spellbooks", spellResourceLocation.getPath());
        }

        AbstractSpell spell = SpellRegistry.getSpell(spellResourceLocation);
        if (spell == null || "none".equals(spell.getSpellName())) {
            MedievalOrigins.LOG.info("No valid spell found for resource location " + spellResourceLocation);
            return;
        }
        
        castSpell(caster, spell, powerLevel, livingTarget, castTime, manaCost, continuousCost, costInterval);
    }

    /**
     * Casts a spell with a specific target and advanced options.
     */
    public static void castSpell(LivingEntity caster, AbstractSpell spell, int spellLevel, LivingEntity target,
                                  Optional<Integer> castTime, Optional<Integer> manaCost, 
                                  boolean continuousCost, int costInterval) {
        if (caster.level().isClientSide()) {
            return;
        }

        MagicData magicData = MagicData.getPlayerMagicData(caster);
        if (magicData.isCasting()) {
            MedievalOrigins.LOG.debug("SpellTriggerAffix: Entity is still casting {}, forcing spell completion", magicData.getCastingSpellId());
            AbstractSpell oldSpell = magicData.getCastingSpell().getSpell();
            oldSpell.onCast(caster.level(), magicData.getCastingSpellLevel(), caster, magicData.getCastSource(), magicData);
            oldSpell.onServerCastComplete(caster.level(), magicData.getCastingSpellLevel(), caster, magicData, false);
            magicData.resetCastingState();
            magicData = MagicData.getPlayerMagicData(caster);
        }

        if (target != null) {
            MedievalOrigins.LOG.debug("SpellTriggerAffix: Merging target data, target: {}", target.getName().getString());
            updateTargetData(caster, target, magicData, spell, x -> true);
        }

        if (caster instanceof ServerPlayer serverPlayer) {
            MedievalOrigins.LOG.debug("Casting SPELL FOR SERVERPLAYA");
            castSpellForPlayer(spell, spellLevel, serverPlayer, magicData, castTime, manaCost, continuousCost, costInterval);
        } else if (caster instanceof IMagicEntity magicEntity) {
            magicEntity.initiateCastSpell(spell, spellLevel);
        } else if (caster instanceof LivingEntity) {
            if (spell.checkPreCastConditions(caster.level(), spellLevel, caster, magicData)) {
                spell.onCast(caster.level(), spellLevel, caster, CastSource.COMMAND, magicData);
                spell.onServerCastComplete(caster.level(), spellLevel, caster, magicData, false);
            }
        }
    }

    private static void castSpellForPlayer(AbstractSpell spell, int spellLevel, ServerPlayer serverPlayer, MagicData magicData,
                                           Optional<Integer> castTime, Optional<Integer> manaCost, 
                                           boolean continuousCost, int costInterval) {

        CastResult castResult = spell.canBeCastedBy(spellLevel, CastSource.COMMAND, magicData, serverPlayer);
        if (castResult.message != null) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(castResult.message));
        }

        // Shouldn't happen
        if (magicData.isCasting()) {
            MedievalOrigins.LOG.warn("Attempted to trigger affix-cast while player was already casting");
            return;
        }

        // Handle mana cost if specified
        if (manaCost.isPresent()) {
            int cost = manaCost.get();
            if (!serverPlayer.getAbilities().instabuild && magicData.getMana() < cost) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("ui.irons_spellbooks.cast_error_mana").withStyle(ChatFormatting.RED)));
                return;
            }
            if (!serverPlayer.getAbilities().instabuild) {
                magicData.setMana(magicData.getMana() - cost);
            }
        }

        // No precast conditions check here; we set a target manually, and ignore default mana cost/cooldowns
        if (serverPlayer.isUsingItem()) {
            serverPlayer.stopUsingItem();
        }

        int effectiveCastTime = 0;
        if (spell.getCastType() == CastType.CONTINUOUS) {
            effectiveCastTime = spell.getEffectiveCastTime(spellLevel, serverPlayer);
        }
        
        // Override cast time if specified
        if (castTime.isPresent()) {
            effectiveCastTime = castTime.get();
        }

        // Setup continuous mana drain if enabled
        if (continuousCost && manaCost.isPresent() && !serverPlayer.getAbilities().instabuild) {
            CONTINUOUS_CASTS.put(serverPlayer.getUUID(), new ContinuousCastData(manaCost.get(), costInterval, 0));
        }

        magicData.initiateCast(spell, spellLevel, effectiveCastTime, CastSource.COMMAND, "command");
        magicData.setPlayerCastingItem(ItemStack.EMPTY);

        spell.onServerPreCast(serverPlayer.level(), spellLevel, serverPlayer, magicData);

        Messages.sendToPlayer(new ClientboundUpdateCastingState(spell.getSpellId(), spellLevel, effectiveCastTime, CastSource.COMMAND, "command"), serverPlayer);
        Messages.sendToPlayersTrackingEntity(new ClientboundOnCastStarted(serverPlayer.getUUID(), spell.getSpellId(), spellLevel), serverPlayer, true);

        if (magicData.getAdditionalCastData() instanceof TargetEntityCastData targetingData) {
            // OtherworldApoth.LOGGER.debug("Casting Spell {} with target {}", magicData.getCastingSpellId(), targetingData.getTarget((ServerLevel) serverPlayer.level()).getName().getString());
        } else {
            MedievalOrigins.LOG.warn("Tried to merge Targeting Data but was overridden. Current cast data for spell {}: {}", magicData.getCastingSpellId(), magicData.getAdditionalCastData().getClass().getName());
        }

        // For instant cast spells, we need to execute them immediately
        // For spells with cast time > 0, the normal tick handler will execute them
        if (effectiveCastTime == 0) {
            spell.onCast(serverPlayer.level(), spellLevel, serverPlayer, CastSource.COMMAND, magicData);
            Messages.sendToPlayer(new ClientboundOnClientCast(spell.getSpellId(), spellLevel, CastSource.COMMAND, magicData.getAdditionalCastData()), serverPlayer);
        }
    }

    /**
     * Should be called every tick for players casting spells to handle continuous mana drain.
     */
    public static void onSpellTick(ServerPlayer player, MagicData magicData) {
        UUID playerId = player.getUUID();
        ContinuousCastData data = CONTINUOUS_CASTS.get(playerId);
        if (data != null) {
            data.ticksElapsed++;
            if (data.ticksElapsed >= data.costInterval) {
                data.ticksElapsed = 0;
                if (magicData.getMana() >= data.manaCost) {
                    magicData.setMana(magicData.getMana() - data.manaCost);
                    MedievalOrigins.LOG.debug("Draining mana: " + data.manaCost + ". Remaining mana: " + magicData.getMana());
                } else {
                    Utils.serverSideCancelCast(player);
                    CONTINUOUS_CASTS.remove(playerId);
                }
            }
        }
    }

    /**
     * Should be called when a spell cast ends to clean up continuous cast tracking.
     */
    public static void onSpellEnd(ServerPlayer player) {
        CONTINUOUS_CASTS.remove(player.getUUID());
    }

    /**
     * Calculates spell damage scaling based on Iron's Spellbooks magic school power.
     * Similar to SpellPowerUtils.scaleAsSpell but for Iron's Spellbooks.
     * 
     * @param magicSchoolStr The magic school identifier (e.g., "fire", "ice", "frost")
     * @param actor The entity casting the spell
     * @return The spell power value for the given school and entity
     */
    public static double getSpellPowerForSchool(String magicSchoolStr, LivingEntity actor) {
        // School translation map for compatibility with different naming conventions
        java.util.Map<String, String> schoolTranslationMap = java.util.Map.of(
            "frost", "ice",
            "healing", "holy",
            "arcane", "ender",
            "soul", "blood"
        );
        
        ResourceLocation schoolResourceLocation = new ResourceLocation(
            magicSchoolStr.contains(":") ? magicSchoolStr : "irons_spellbooks:" + magicSchoolStr
        );
        
        SchoolType magicSchool = SchoolRegistry.getSchool(schoolResourceLocation);
        
        if (magicSchool == null) {
            // Try translated school name
            String translatedSchool = schoolTranslationMap.get(magicSchoolStr);
            if (translatedSchool != null) {
                schoolResourceLocation = new ResourceLocation(
                    translatedSchool.contains(":") ? translatedSchool : "irons_spellbooks:" + translatedSchool
                );
                magicSchool = SchoolRegistry.getSchool(schoolResourceLocation);
            }
        }
        
        if (magicSchool != null) {
            return magicSchool.getPowerFor(actor);
        } else {
            MedievalOrigins.LOG.info("No valid Magic School found for type " + magicSchoolStr);
            return 0.0;
        }
    }

    public static void updateTargetData(LivingEntity caster, Entity entityHit, MagicData playerMagicData, AbstractSpell spell, Predicate<LivingEntity> filter) {
        LivingEntity livingTarget = null;
        if (entityHit instanceof LivingEntity livingEntity && filter.test(livingEntity)) {
            livingTarget = livingEntity;
        } else {
            // Try to get parent entity via reflection (for Forge's PartEntity)
            Entity parent = getParentFromPartEntity(entityHit);
            if (parent instanceof LivingEntity livingParent && filter.test(livingParent)) {
                livingTarget = livingParent;
            }
        }

        if (livingTarget != null) {
            playerMagicData.setAdditionalCastData(new TargetEntityCastData(livingTarget));
            if (caster instanceof ServerPlayer serverPlayer) {
                if (spell.getCastType() != CastType.INSTANT) {
                    Messages.sendToPlayer(new ClientboundSyncTargetingData(livingTarget, spell), serverPlayer);
                }
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.spell_target_success", livingTarget.getDisplayName().getString(), spell.getDisplayName(serverPlayer)).withStyle(ChatFormatting.GREEN)));
            }
            if (livingTarget instanceof ServerPlayer serverPlayer) {
                Utils.sendTargetedNotification(serverPlayer, caster, spell);
            }
        } else if (caster instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.cast_error_target").withStyle(ChatFormatting.RED)));
        }
    }
}
