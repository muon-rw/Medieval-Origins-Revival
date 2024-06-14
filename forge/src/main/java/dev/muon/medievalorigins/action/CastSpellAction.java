package dev.muon.medievalorigins.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.Constants;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
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


import java.util.Optional;

public class CastSpellAction extends EntityAction<CastSpellAction.Configuration> {

    public CastSpellAction() {
        super(Configuration.CODEC);
    }

    @Override
    public void execute(Configuration configuration, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            Constants.LOG.info("Entity is not a LivingEntity: " + entity);
            return;
        }

        String spellStr = configuration.spell().toString();
        ResourceLocation spellResourceLocation = new ResourceLocation(spellStr);
        if ("minecraft".equals(spellResourceLocation.getNamespace())) {
            spellResourceLocation = new ResourceLocation("irons_spellbooks", spellResourceLocation.getPath());
        }

        AbstractSpell spell = SpellRegistry.getSpell(spellResourceLocation);
        if (spell == null || "none".equals(spell.getSpellName())) {
            Constants.LOG.info("No valid spell found for resource location " + spellResourceLocation);
            return;
        }

        Level world = entity.getCommandSenderWorld();
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
                    if (magicData.getMana() < manaCost) {
                        Messages.sendToPlayer(new ClientboundCastErrorMessage(ClientboundCastErrorMessage.ErrorType.MANA, spell), serverPlayer);
                        return;
                    }
                    magicData.setMana(magicData.getMana() - manaCost);
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
            Constants.LOG.info("Entity is not a valid caster (currently only players can cast spells with this entity action): " + entity);
        }
    }

    public static class Configuration implements IDynamicFeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SerializableDataTypes.IDENTIFIER.fieldOf("spell").forGetter(Configuration::spell),
                Codec.INT.optionalFieldOf("power_level", 1).forGetter(Configuration::powerLevel),
                Codec.INT.optionalFieldOf("cast_time").forGetter(Configuration::castTime),
                Codec.INT.optionalFieldOf("mana_cost").forGetter(Configuration::manaCost)
        ).apply(instance, Configuration::new));

        private final ResourceLocation spell;
        private final int powerLevel;
        private final Optional<Integer> castTime;
        private final Optional<Integer> manaCost;

        public Configuration(ResourceLocation spell, int powerLevel, Optional<Integer> castTime, Optional<Integer> manaCost) {
            this.spell = spell;
            this.powerLevel = powerLevel;
            this.castTime = castTime;
            this.manaCost = manaCost;
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
    }
}