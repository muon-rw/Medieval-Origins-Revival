package dev.muon.medievalorigins.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.MedievalOrigins;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.redspace.ironsspellbooks.api.events.ChangeManaEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.network.ClientboundCastErrorMessage;
import io.redspace.ironsspellbooks.network.ClientboundUpdateCastingState;
import io.redspace.ironsspellbooks.network.spell.ClientboundOnCastStarted;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CastSpellAction extends EntityAction<CastSpellAction.Configuration> {
    private static final Map<UUID, ContinuousCastData> CONTINUOUS_CASTS = new HashMap<>();

    public CastSpellAction() {
        super(Configuration.CODEC);
    }

    @Override
    public void execute(Configuration configuration, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            MedievalOrigins.LOGGER.info("Entity is not a LivingEntity: " + entity);
            return;
        }

        String spellStr = configuration.spell().toString();
        ResourceLocation spellResourceLocation = new ResourceLocation(spellStr);
        // No one should be using the minecraft namespace anyway, and this is simpler
        if ("minecraft".equals(spellResourceLocation.getNamespace())) {
            spellResourceLocation = new ResourceLocation("irons_spellbooks", spellResourceLocation.getPath());
        }

        AbstractSpell spell = SpellRegistry.getSpell(spellResourceLocation);
        if (spell == null || "none".equals(spell.getSpellName())) {
            MedievalOrigins.LOGGER.info("No valid spell found for resource location " + spellResourceLocation);
            return;
        }

        Level world = entity.level();
        if (world.isClientSide) {
            return;
        }

        int powerLevel = configuration.powerLevel();

        Optional<Integer> castTimeOpt = configuration.castTime();
        Optional<Integer> manaCostOpt = configuration.manaCost();

        if (entity instanceof ServerPlayer serverPlayer) {
            MagicData magicData = MagicData.getPlayerMagicData(serverPlayer);
            if (!magicData.isCasting()) {
                CastResult castResult = spell.canBeCastedBy(powerLevel, CastSource.COMMAND, magicData, serverPlayer);
                if (castResult.message != null) {
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(castResult.message));
                }

                if (!castResult.isSuccess() || !spell.checkPreCastConditions(world, powerLevel, serverPlayer, magicData) || MinecraftForge.EVENT_BUS.post(new SpellPreCastEvent(serverPlayer, spell.getSpellId(), powerLevel, spell.getSchoolType(), CastSource.COMMAND))) {
                    return;
                }

                if (serverPlayer.isUsingItem()) {
                    serverPlayer.stopUsingItem();
                }


                int effectiveCastTime = spell.getEffectiveCastTime(powerLevel, serverPlayer);
                if (castTimeOpt.isPresent()) {
                    int castTime = castTimeOpt.get();
                    effectiveCastTime = castTime;
                }

                if (manaCostOpt.isPresent()) {
                    int manaCost = manaCostOpt.get();
                    if (!serverPlayer.getAbilities().instabuild && magicData.getMana() < manaCost) {
                        Messages.sendToPlayer(new ClientboundCastErrorMessage(ClientboundCastErrorMessage.ErrorType.MANA, spell), serverPlayer);
                        return;
                    }
                    if (!serverPlayer.getAbilities().instabuild) {
                        setManaWithEvent(serverPlayer, magicData, magicData.getMana() - manaCost);
                    }
                }

                if (configuration.continuousCost() && manaCostOpt.isPresent() && !serverPlayer.getAbilities().instabuild) {
                    int manaCost = manaCostOpt.get();
                    int costInterval = configuration.costInterval();
                    CONTINUOUS_CASTS.put(serverPlayer.getUUID(), new ContinuousCastData(manaCost, costInterval, 0));
                }


                magicData.initiateCast(spell, powerLevel, effectiveCastTime, CastSource.COMMAND, "command");
                magicData.setPlayerCastingItem(serverPlayer.getItemInHand(InteractionHand.MAIN_HAND));

                spell.onServerPreCast(world, powerLevel, serverPlayer, magicData);
                Messages.sendToPlayer(new ClientboundUpdateCastingState(spell.getSpellId(), powerLevel, effectiveCastTime, CastSource.COMMAND, "command"), serverPlayer);
                Messages.sendToPlayersTrackingEntity(new ClientboundOnCastStarted(serverPlayer.getUUID(), spell.getSpellId(), powerLevel), serverPlayer, true);

            } else {
                Utils.serverSideCancelCast(serverPlayer);
            }
        } else {
            MedievalOrigins.LOGGER.info("Entity is not a valid caster (currently only players can cast spells with this entity action): " + entity);
        }
    }

    public static void onSpellTick(ServerPlayer player, MagicData magicData) {
        UUID playerId = player.getUUID();
        ContinuousCastData data = CONTINUOUS_CASTS.get(playerId);
        if (data != null) {
            data.ticksElapsed++;
            if (data.ticksElapsed >= data.costInterval) {
                data.ticksElapsed = 0;
                if (magicData.getMana() >= data.manaCost) {
                    setManaWithEvent(player, magicData, magicData.getMana() - data.manaCost);
                    MedievalOrigins.LOGGER.info("Draining mana: " + data.manaCost + ". Remaining mana: " + magicData.getMana());
                } else {
                    Utils.serverSideCancelCast(player);
                    CONTINUOUS_CASTS.remove(playerId);
                }
            }
        }
    }

    private static void setManaWithEvent(ServerPlayer player, MagicData magicData, float newMana) {
        ChangeManaEvent event = new ChangeManaEvent(player, magicData, magicData.getMana(), newMana);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            magicData.setMana(event.getNewMana());
        }
    }

    public static void onSpellEnd(ServerPlayer player) {
        CONTINUOUS_CASTS.remove(player.getUUID());
    }

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

    public static class Configuration implements IDynamicFeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SerializableDataTypes.IDENTIFIER.fieldOf("spell").forGetter(Configuration::spell),
                Codec.INT.optionalFieldOf("power_level", 1).forGetter(Configuration::powerLevel),
                Codec.INT.optionalFieldOf("cast_time").forGetter(Configuration::castTime),
                Codec.INT.optionalFieldOf("mana_cost").forGetter(Configuration::manaCost),
                Codec.BOOL.optionalFieldOf("continuous_cost", false).forGetter(Configuration::continuousCost),
                Codec.INT.optionalFieldOf("cost_interval", 20).forGetter(Configuration::costInterval)
        ).apply(instance, Configuration::new));

        private final ResourceLocation spell;
        private final int powerLevel;
        private final Optional<Integer> castTime;
        private final Optional<Integer> manaCost;
        private final boolean continuousCost;
        private final int costInterval;

        public Configuration(ResourceLocation spell, int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost, boolean continuousCost, int costInterval) {
            this.spell = spell;
            this.powerLevel = powerLevel;
            this.castTime = castTime;
            this.manaCost = manaCost;
            this.continuousCost = continuousCost;
            this.costInterval = costInterval;
        }


        public ResourceLocation spell() {
            return spell;
        }

        public int powerLevel() {
            return powerLevel;
        }

        public Optional<Integer> castTime() {
            return castTime;
        }

        public Optional<Integer> manaCost() {
            return manaCost;
        }

        public boolean continuousCost() {
            return continuousCost;
        }

        public int costInterval() {
            return costInterval;
        }
    }
}