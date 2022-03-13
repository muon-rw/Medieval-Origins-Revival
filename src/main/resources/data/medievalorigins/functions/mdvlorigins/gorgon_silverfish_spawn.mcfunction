summon minecraft:silverfish ~ ~ ~ {Tags:["medievalorigins_despawn"]}
team join SilverfishAlly @e[type=minecraft:silverfish]
team join SilverfishEnemy @e[type=!minecraft:player,type=!minecraft:silverfish,distance=..8]
execute at @e[type=minecraft:silverfish,distance=..8] run summon minecraft:iron_golem ~ ~1 ~ {Tags:["medievalorigins_despawn"],ActiveEffects:[{Id:14,Amplifier:255,Duration:2147483647,ShowParticles:0b},{Id:18,Amplifier:255,Duration:2147483647,ShowParticles:0b}],Silent:1,DeathLootTable:"minecraft:empty",Attributes:[{Name:generic.attack_damage,Base:1.5}]}
team join SilverfishAlly @e[type=minecraft:iron_golem,tag=medievalorigins_despawn]