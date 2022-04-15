fill ~-1.25 ~-1.25 ~-1.25 ~1.25 ~1.25 ~1.25 black_glazed_terracotta replace bedrock
fill ~-1.25 ~-1.25 ~-1.25 ~1.25 ~1.25 ~1.25 lime_glazed_terracotta replace end_portal_frame[facing=east]
fill ~-1.25 ~-1.25 ~-1.25 ~1.25 ~1.25 ~1.25 blue_glazed_terracotta replace end_portal_frame[facing=north]
fill ~-1.25 ~-1.25 ~-1.25 ~1.25 ~1.25 ~1.25 brown_glazed_terracotta replace end_portal_frame[facing=west]
fill ~-1.25 ~-1.25 ~-1.25 ~1.25 ~1.25 ~1.25 pink_glazed_terracotta replace end_portal_frame[facing=south]
fill ~-1.25 ~-1.25 ~-1.25 ~1.25 ~1.25 ~1.25 air destroy
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