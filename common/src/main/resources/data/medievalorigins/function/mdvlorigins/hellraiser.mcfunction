summon medievalorigins:summon_skeleton ~3 ~ ~ {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-90.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.75 ~ ~1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-67.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.1 ~ ~2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-45.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~1.1 ~ ~2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-22.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~ ~ ~3 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-0.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-1.1 ~ ~2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[22.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.1 ~ ~2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[45.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.75 ~ ~1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[67.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~-3 ~ ~ {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[90.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.75 ~ ~-1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[112.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.1 ~ ~-2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[135.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-1.1 ~ ~-2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[157.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~ ~ ~-3 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[180.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~1.1 ~ ~-2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-157.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.1 ~ ~-2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-135.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.75 ~ ~-1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:150,isLimited:1b,Rotation:[-112.5f,-45.0f]}
team join SkeletonCircle @e[tag=necroskelwall,distance=..5]
execute as @e[tag=necroskelwall,distance=..8] at @s run summon area_effect_cloud ~ ~ ~ {Particle:{type:soul},Radius:1.6,RadiusPerTick:-0.0033,Duration:70,potion_contents:{potion:harming}}
playsound minecraft:entity.skeleton.death player @s