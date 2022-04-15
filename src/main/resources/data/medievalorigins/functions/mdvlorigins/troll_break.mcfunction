execute if entity @s[y_rotation=75..105] run fill ~ ~ ~-0.5 ~-1 ~3 ~0.5 black_glazed_terracotta replace bedrock
execute if entity @s[y_rotation=75..105] run fill ~ ~ ~-0.5 ~-1 ~3 ~0.5 lime_glazed_terracotta replace end_portal_frame[facing=east]
execute if entity @s[y_rotation=75..105] run fill ~ ~ ~-0.5 ~-1 ~3 ~0.5 blue_glazed_terracotta replace end_portal_frame[facing=north]
execute if entity @s[y_rotation=75..105] run fill ~ ~ ~-0.5 ~-1 ~3 ~0.5 brown_glazed_terracotta replace end_portal_frame[facing=west]
execute if entity @s[y_rotation=75..105] run fill ~ ~ ~-0.5 ~-1 ~3 ~0.5 pink_glazed_terracotta replace end_portal_frame[facing=south]
execute if entity @s[y_rotation=75..105] run fill ~ ~ ~-0.5 ~-1 ~3 ~0.5 air destroy
execute if entity @s[y_rotation=-105..-75] run fill ~ ~ ~-0.5 ~1 ~3 ~0.5 black_glazed_terracotta replace bedrock
execute if entity @s[y_rotation=-105..-75] run fill ~ ~ ~-0.5 ~1 ~3 ~0.5 lime_glazed_terracotta replace end_portal_frame[facing=east]
execute if entity @s[y_rotation=-105..-75] run fill ~ ~ ~-0.5 ~1 ~3 ~0.5 blue_glazed_terracotta replace end_portal_frame[facing=north]
execute if entity @s[y_rotation=-105..-75] run fill ~ ~ ~-0.5 ~1 ~3 ~0.5 brown_glazed_terracotta replace end_portal_frame[facing=west]
execute if entity @s[y_rotation=-105..-75] run fill ~ ~ ~-0.5 ~1 ~3 ~0.5 pink_glazed_terracotta replace end_portal_frame[facing=south]
execute if entity @s[y_rotation=-105..-75] run fill ~ ~ ~-0.5 ~1 ~3 ~0.5 air destroy
execute if entity @s[y_rotation=165..195] run fill ~-0.5 ~ ~ ~0.5 ~3 ~-1 black_glazed_terracotta replace bedrock
execute if entity @s[y_rotation=165..195] run fill ~-0.5 ~ ~ ~0.5 ~3 ~-1 lime_glazed_terracotta replace end_portal_frame[facing=east]
execute if entity @s[y_rotation=165..195] run fill ~-0.5 ~ ~ ~0.5 ~3 ~-1 blue_glazed_terracotta replace end_portal_frame[facing=north]
execute if entity @s[y_rotation=165..195] run fill ~-0.5 ~ ~ ~0.5 ~3 ~-1 brown_glazed_terracotta replace end_portal_frame[facing=west]
execute if entity @s[y_rotation=165..195] run fill ~-0.5 ~ ~ ~0.5 ~3 ~-1 pink_glazed_terracotta replace end_portal_frame[facing=south]
execute if entity @s[y_rotation=165..195] run fill ~-0.5 ~ ~ ~0.5 ~3 ~-1 air destroy
execute if entity @s[y_rotation=-15..15] run fill ~-0.5 ~ ~ ~0.5 ~3 ~1 black_glazed_terracotta replace bedrock
execute if entity @s[y_rotation=-15..15] run fill ~-0.5 ~ ~ ~0.5 ~3 ~1 lime_glazed_terracotta replace end_portal_frame[facing=east]
execute if entity @s[y_rotation=-15..15] run fill ~-0.5 ~ ~ ~0.5 ~3 ~1 blue_glazed_terracotta replace end_portal_frame[facing=north]
execute if entity @s[y_rotation=-15..15] run fill ~-0.5 ~ ~ ~0.5 ~3 ~1 brown_glazed_terracotta replace end_portal_frame[facing=west]
execute if entity @s[y_rotation=-15..15] run fill ~-0.5 ~ ~ ~0.5 ~3 ~1 pink_glazed_terracotta replace end_portal_frame[facing=south]
execute if entity @s[y_rotation=-15..15] run fill ~-0.5 ~ ~ ~0.5 ~3 ~1 air destroy
execute at @e[type=item,distance=..12,nbt={Item:{id:"minecraft:black_glazed_terracotta"}}] run setblock ~ ~ ~ bedrock
execute at @e[type=item,distance=..12,nbt={Item:{id:"minecraft:lime_glazed_terracotta"}}] run setblock ~ ~ ~ end_portal_frame[facing=east]
execute at @e[type=item,distance=..12,nbt={Item:{id:"minecraft:blue_glazed_terracotta"}}] run setblock ~ ~ ~ end_portal_frame[facing=north]
execute at @e[type=item,distance=..12,nbt={Item:{id:"minecraft:brown_glazed_terracotta"}}] run setblock ~ ~ ~ end_portal_frame[facing=west]
execute at @e[type=item,distance=..12,nbt={Item:{id:"minecraft:pink_glazed_terracotta"}}] run setblock ~ ~ ~ end_portal_frame[facing=south]
execute at @e[type=item,distance=..12,nbt={Item:{id:"minecraft:obsidian"}}] run setblock ~ ~ ~ obsidian
kill @e[type=item,distance=..12,nbt={Item:{id:"minecraft:black_glazed_terracotta"}}]
kill @e[type=item,distance=..12,nbt={Item:{id:"minecraft:lime_glazed_terracotta"}}]
kill @e[type=item,distance=..12,nbt={Item:{id:"minecraft:blue_glazed_terracotta"}}]
kill @e[type=item,distance=..12,nbt={Item:{id:"minecraft:brown_glazed_terracotta"}}]
kill @e[type=item,distance=..12,nbt={Item:{id:"minecraft:pink_glazed_terracotta"}}]
kill @e[type=item,distance=..12,nbt={Item:{id:"minecraft:obsidian"}}]
resource set @s medievalorigins:troll/thunderous_stride_thunderous_stride_resource 0