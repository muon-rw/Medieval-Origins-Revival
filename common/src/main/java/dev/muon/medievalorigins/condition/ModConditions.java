package dev.muon.medievalorigins.condition;

import dev.muon.medievalorigins.MedievalOrigins;
import dev.muon.medievalorigins.condition.bientity.AlliedCondition;
import dev.muon.medievalorigins.condition.block.IsCropCondition;
import dev.muon.medievalorigins.condition.block.IsFlowerCondition;
import dev.muon.medievalorigins.condition.entity.CoveredByBlockCondition;
import dev.muon.medievalorigins.condition.entity.CreativeModeCondition;
import dev.muon.medievalorigins.condition.entity.EntityInRadiusCondition;
import dev.muon.medievalorigins.condition.entity.IsArrowCondition;
import dev.muon.medievalorigins.condition.entity.LookingDownCondition;
import dev.muon.medievalorigins.condition.entity.OptionalDimensionCondition;
import dev.muon.medievalorigins.condition.item.IsAxeCondition;
import dev.muon.medievalorigins.condition.item.IsBowCondition;
import dev.muon.medievalorigins.condition.item.IsCursedCondition;
import dev.muon.medievalorigins.condition.item.IsDaggerCondition;
import dev.muon.medievalorigins.condition.item.IsFistWeaponCondition;
import dev.muon.medievalorigins.condition.item.IsGoldenArmorCondition;
import dev.muon.medievalorigins.condition.item.IsGoldenToolCondition;
import dev.muon.medievalorigins.condition.item.IsGoldenWeaponCondition;
import dev.muon.medievalorigins.condition.item.IsHeavyArmorCondition;
import dev.muon.medievalorigins.condition.item.IsMeleeWeaponCondition;
import dev.muon.medievalorigins.condition.item.IsSilverArmorCondition;
import dev.muon.medievalorigins.condition.item.IsSilverToolCondition;
import dev.muon.medievalorigins.condition.item.IsSilverWeaponCondition;
import dev.muon.medievalorigins.condition.item.IsSummonEquipmentCondition;
import dev.muon.medievalorigins.condition.item.IsToolCondition;
import dev.muon.medievalorigins.condition.item.IsValkyrieWeaponCondition;
import dev.overgrown.apoli.condition.ConditionTypes;

public class ModConditions {

    public static void register() {
        ConditionTypes.BI_ENTITY.register(MedievalOrigins.loc("allied"), new AlliedCondition());

        ConditionTypes.BLOCK.register(MedievalOrigins.loc("is_crop"), new IsCropCondition());
        ConditionTypes.BLOCK.register(MedievalOrigins.loc("is_flower"), new IsFlowerCondition());

        ConditionTypes.ENTITY.register(MedievalOrigins.loc("is_arrow"), new IsArrowCondition());
        ConditionTypes.ENTITY.register(MedievalOrigins.loc("entity_in_radius"), new EntityInRadiusCondition());
        ConditionTypes.ENTITY.register(MedievalOrigins.loc("creative_mode"), new CreativeModeCondition());
        ConditionTypes.ENTITY.register(MedievalOrigins.loc("dimension"), new OptionalDimensionCondition());
        ConditionTypes.ENTITY.register(MedievalOrigins.loc("covered_by_block"), new CoveredByBlockCondition());
        ConditionTypes.ENTITY.register(MedievalOrigins.loc("looking_down"), new LookingDownCondition());

        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_axe"), new IsAxeCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_bow"), new IsBowCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_cursed"), new IsCursedCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_dagger"), new IsDaggerCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_fist_weapon"), new IsFistWeaponCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_golden_armor"), new IsGoldenArmorCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_golden_tool"), new IsGoldenToolCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_golden_weapon"), new IsGoldenWeaponCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_heavy_armor"), new IsHeavyArmorCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_melee_weapon"), new IsMeleeWeaponCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_silver_armor"), new IsSilverArmorCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_silver_tool"), new IsSilverToolCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_silver_weapon"), new IsSilverWeaponCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_summon_equipment"), new IsSummonEquipmentCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_tool"), new IsToolCondition());
        ConditionTypes.ITEM.register(MedievalOrigins.loc("is_valkyrie_weapon"), new IsValkyrieWeaponCondition());
    }
}