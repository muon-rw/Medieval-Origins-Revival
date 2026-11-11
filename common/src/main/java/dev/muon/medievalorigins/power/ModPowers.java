package dev.muon.medievalorigins.power;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.platform.Services;
import dev.overgrown.apoli.power.PowerTypeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class ModPowers {
    public static final ResourceLocation PIXIE_WINGS = MedievalOrigins.loc("pixie_wings");
    public static final ResourceLocation FAE_WINGS = MedievalOrigins.loc("fae_wings");
    public static final ResourceLocation ICARUS_WINGS = MedievalOrigins.loc("icarus_wings");
    public static final ResourceLocation OWNER_ATTRIBUTE_TRANSFER = MedievalOrigins.loc("owner_attribute_transfer");
    public static final ResourceLocation MOBS_IGNORE = MedievalOrigins.loc("mobs_ignore");
    public static final ResourceLocation ACTION_ON_TARGET_DEATH = MedievalOrigins.loc("action_on_target_death");
    public static final ResourceLocation CUSTOM_DEATH_SOUND = MedievalOrigins.loc("custom_death_sound");
    public static final ResourceLocation ACTION_ON_JUMP = MedievalOrigins.loc("action_on_jump");
    public static final ResourceLocation PREVENT_CROP_TRAMPLE = MedievalOrigins.loc("prevent_crop_trample");
    public static final ResourceLocation MODIFY_PROJECTILE_VELOCITY = MedievalOrigins.loc("modify_projectile_velocity");
    public static final ResourceLocation MODIFY_PIERCE_LEVEL = MedievalOrigins.loc("modify_pierce_level");
    public static final ResourceLocation MODIFY_PROJECTILE_ACCURACY = MedievalOrigins.loc("modify_projectile_accuracy");
    public static final ResourceLocation MODIFY_REPUTATION = MedievalOrigins.loc("modify_reputation");
    public static final ResourceLocation MODIFY_INSOMNIA_TICKS = MedievalOrigins.loc("modify_insomnia_ticks");

    private ModPowers() {
    }

    public static void register() {
        PowerTypeRegistry.register(PIXIE_WINGS, new PixieWingsPower());
        PowerTypeRegistry.register(FAE_WINGS, new FaeWingsPower());
        if (Services.PLATFORM.isModLoaded("icarus")) {
            PowerTypeRegistry.register(ICARUS_WINGS, new IcarusWingsPower());
        }
        PowerTypeRegistry.register(OWNER_ATTRIBUTE_TRANSFER, new OwnerAttributeTransferPower());
        PowerTypeRegistry.register(MOBS_IGNORE, new MobsIgnorePower());
        PowerTypeRegistry.register(ACTION_ON_TARGET_DEATH, new ActionOnTargetDeathPower());
        PowerTypeRegistry.register(CUSTOM_DEATH_SOUND, new CustomDeathSoundPower());
        PowerTypeRegistry.register(ACTION_ON_JUMP, new ActionOnJumpPower());
        PowerTypeRegistry.register(PREVENT_CROP_TRAMPLE, new PreventCropTramplePower());
        PowerTypeRegistry.register(MODIFY_PROJECTILE_VELOCITY, new ModifyProjectileVelocityPower());
        PowerTypeRegistry.register(MODIFY_PIERCE_LEVEL, new ModifyPierceLevelPower());
        PowerTypeRegistry.register(MODIFY_PROJECTILE_ACCURACY, new ModifyProjectileAccuracyPower());
        PowerTypeRegistry.register(MODIFY_REPUTATION, new ModifyReputationPower());
        PowerTypeRegistry.register(MODIFY_INSOMNIA_TICKS, new ModifyInsomniaTicksPower());
    }
}
