summon medievalorigins:summon_skeleton ~3 ~ ~ {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-90.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.75 ~ ~1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-67.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.1 ~ ~2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-45.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~1.1 ~ ~2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-22.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~ ~ ~3 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-0.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-1.1 ~ ~2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[22.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.1 ~ ~2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[45.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.75 ~ ~1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[67.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~-3 ~ ~ {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[90.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.75 ~ ~-1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[112.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~-2.1 ~ ~-2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[135.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~-1.1 ~ ~-2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[157.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~ ~ ~-3 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[180.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~1.1 ~ ~-2.75 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-157.5f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.1 ~ ~-2.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-135.0f,-45.0f]}
summon medievalorigins:summon_skeleton ~2.75 ~ ~-1.1 {Tags:["necroskelwall"],NoAI:1,LifeTicks:200,Rotation:[-112.5f,-45.0f]}
team join SkeletonCircle @e[tag=necroskelwall,distance=..5]
execute as @e[tag=necroskelwall,distance=..8] at @s run summon area_effect_cloud ~ ~.75 ~ {Radius:1.8f,Duration:3000,RadiusOnUse:-0.0033f,RadiusPerTick:-0.0033f,ReapplicationDelay:40,Effects:[{Duration:80,Id:20b,Amplifier:2,IsAmbient:true,ShowParticles:false,ShowIcon:false}]}
playsound minecraft:entity.skeleton.death player @s