execute as @e[type=!minecraft:wither,type=!minecraft:ender_dragon] at @s if entity @e[type=minecraft:player,limit=1] run data modify entity @s AngryTime set value 40000
execute as @e[type=!minecraft:wither,type=!minecraft:ender_dragon] at @s if entity @e[type=minecraft:player,limit=1] run data modify entity @s AngryAt set from entity @e[type=player,limit=1] UUID
team leave @s