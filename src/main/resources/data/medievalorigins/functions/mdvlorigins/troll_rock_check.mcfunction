execute if score @s CobbleCheck matches 1..63 run give @s minecraft:cobblestone
execute if score @s CobbleCheck matches 1..63 run scoreboard players remove @s CobbleCheck 1
execute if score @s CobbleCheck matches 64 run function medievalorigins:mdvlorigins/troll_rock_spawn
execute if score @s CobbleCheck matches 1..63 run function medievalorigins:mdvlorigins/troll_rock_check