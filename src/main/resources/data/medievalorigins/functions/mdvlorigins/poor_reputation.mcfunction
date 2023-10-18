execute as @e[type=minecraft:iron_golem,distance=..24] at @s if entity @e[type=minecraft:player,predicate=medievalorigins:class/necromancer,limit=1,distance=..16] run data modify entity @s AngerTime set value 40000
execute as @e[type=minecraft:iron_golem,distance=..16] at @s if entity @e[type=minecraft:player,limit=1,distance=..16] run data modify entity @s AngryAt set from entity @e[type=player,predicate=medievalorigins:class/necromancer,distance=..16,limit=1] UUID
team join PillagerAlly @s
team join PillagerAlly @e[type=pillager]
team join PillagerAlly @e[type=ravager]
team join PillagerAlly @e[type=evoker]
team join PillagerAlly @e[type=vindicator]
team join PillagerAlly @e[type=vex]