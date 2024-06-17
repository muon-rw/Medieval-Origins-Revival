data modify entity @s NoAI set value 0
fill ~-1 ~-2 ~-1 ~1 ~2 ~1 minecraft:air replace minecraft:blue_ice
tag @s remove MedievaloriginsYetiFrozen
execute at @s run playsound minecraft:block.glass.break block @a ~ ~ ~
particle minecraft:block minecraft:blue_ice ~ ~ ~
power revoke @s medievalorigins:yeti/frozen_entity