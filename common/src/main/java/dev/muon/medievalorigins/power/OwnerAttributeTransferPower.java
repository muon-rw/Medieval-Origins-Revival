package dev.muon.medievalorigins.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.entity.SummonedMob;
import dev.overgrown.apoli.condition.context.EntityCtx;
import dev.overgrown.apoli.data.AttributeModifier;
import dev.overgrown.apoli.power.ApoliPowers;
import dev.overgrown.apoli.power.Power;
import dev.overgrown.apoli.power.PowerContainer;
import dev.overgrown.apoli.power.PowerType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;
import java.util.Optional;

/** Scales the summon's modifiers by its owner's value of {@code owner_attribute}. */
public final class OwnerAttributeTransferPower extends PowerType<OwnerAttributeTransferPower.Config> {

    private static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("owner_attribute").forGetter(Config::ownerAttribute),
            AttributeModifier.LIST_OR_SINGLE.fieldOf("modifiers").forGetter(Config::modifiers),
            Codec.BOOL.optionalFieldOf("update_health", true).forGetter(Config::updateHealth),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("tick_rate", 20).forGetter(Config::tickRate)
    ).apply(instance, Config::new));

    @Override
    public MapCodec<Config> configCodec() {
        return CODEC;
    }

    @Override
    public void onAdded(ResourceLocation powerId, Config cfg, PowerContainer holder, ResourceLocation source) {
        if (!holder.isSuppressed(powerId)) {
            refresh(powerId, cfg, holder);
        }
    }

    @Override
    public void tick(ResourceLocation powerId, Config cfg, PowerContainer holder) {
        LivingEntity entity = holder.owner();
        if (entity != null && entity.tickCount % cfg.tickRate() == 0) {
            refresh(powerId, cfg, holder);
        }
    }

    @Override
    public void onSuppressed(ResourceLocation powerId, Config cfg, PowerContainer holder) {
        strip(powerId, cfg, holder.owner());
    }

    @Override
    public void onRemoved(ResourceLocation powerId, Config cfg, PowerContainer holder, ResourceLocation source) {
        if (!holder.hasPower(powerId)) {
            strip(powerId, cfg, holder.owner());
        }
    }

    private static void refresh(ResourceLocation powerId, Config cfg, PowerContainer holder) {
        LivingEntity entity = holder.owner();
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        LivingEntity summoner = entity instanceof SummonedMob summon ? summon.getOwner() : null;
        AttributeInstance summonerAttribute = summoner == null ? null : summoner.getAttribute(cfg.ownerAttribute());
        if (summonerAttribute == null || !conditionHolds(powerId, entity)) {
            strip(powerId, cfg, entity);
            return;
        }
        double ownerValue = summonerAttribute.getValue();
        for (int i = 0; i < cfg.modifiers().size(); i++) {
            AttributeModifier modifier = cfg.modifiers().get(i);
            AttributeInstance instance = attributeInstance(entity, modifier);
            if (instance == null) {
                continue;
            }
            ResourceLocation id = modifierId(powerId, i);
            double amount = modifier.resolveInput(entity, holder, instance.getBaseValue()) * ownerValue;
            var current = instance.getModifier(id);
            if (current != null && Math.abs(current.amount() - amount) < 1.0e-6) {
                continue;
            }
            replace(entity, instance, id, new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                    id, amount, modifier.operation().vanillaOperation()), cfg.updateHealth());
        }
    }

    private static void strip(ResourceLocation powerId, Config cfg, LivingEntity entity) {
        if (entity == null) {
            return;
        }
        for (int i = 0; i < cfg.modifiers().size(); i++) {
            AttributeInstance instance = attributeInstance(entity, cfg.modifiers().get(i));
            ResourceLocation id = modifierId(powerId, i);
            if (instance != null && instance.getModifier(id) != null) {
                replace(entity, instance, id, null, cfg.updateHealth());
            }
        }
    }

    private static void replace(LivingEntity entity, AttributeInstance instance, ResourceLocation id,
                                net.minecraft.world.entity.ai.attributes.AttributeModifier next, boolean updateHealth) {
        boolean maxHealth = instance.getAttribute().is(Attributes.MAX_HEALTH);
        float previousMax = entity.getMaxHealth();
        float previousHealth = entity.getHealth();
        instance.removeModifier(id);
        if (next != null) {
            instance.addTransientModifier(next);
        }
        if (maxHealth && updateHealth && previousMax > 0) {
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getMaxHealth() * previousHealth / previousMax));
        } else if (maxHealth) {
            entity.setHealth(Math.min(previousHealth, entity.getMaxHealth()));
        }
    }

    private static AttributeInstance attributeInstance(LivingEntity entity, AttributeModifier modifier) {
        Optional<Holder.Reference<Attribute>> attribute = modifier.attribute().flatMap(BuiltInRegistries.ATTRIBUTE::getHolder);
        return attribute.map(entity::getAttribute).orElse(null);
    }

    private static ResourceLocation modifierId(ResourceLocation powerId, int index) {
        return MedievalOrigins.loc("owner_attribute_transfer/" + powerId.getNamespace() + "/" + powerId.getPath().replace('/', '_') + "/" + index);
    }

    private static boolean conditionHolds(ResourceLocation powerId, LivingEntity entity) {
        Power power = ApoliPowers.get(powerId);
        return power == null || power.condition().isEmpty() || power.condition().get().test(EntityCtx.of(entity, entity.level()));
    }

    public record Config(Holder<Attribute> ownerAttribute, List<AttributeModifier> modifiers, boolean updateHealth, int tickRate) {
    }
}
