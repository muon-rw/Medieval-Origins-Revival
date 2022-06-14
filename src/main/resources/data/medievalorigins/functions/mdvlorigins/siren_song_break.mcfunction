team leave @s
execute as @e[type=!#medievalorigins:bosses] at @s if entity @e[type=minecraft:player,limit=1] run data modify entity @s AngryTime set value 40000
execute as @e[type=!#medievalorigins:bosses] at @s if entity @e[type=minecraft:player,limit=1] run data modify entity @s AngryAt set from entity @e[type=player,limit=1] UUID