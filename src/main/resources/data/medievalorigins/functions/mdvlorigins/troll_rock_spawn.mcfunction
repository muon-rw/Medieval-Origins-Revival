scoreboard players set @s CobbleCheck 0
summon minecraft:creeper ~ ~ ~ {ExplosionRadius:4,DeathLootTable:"minecraft:empty",Fuse:40s,ignited:1,ActiveEffects:[{Id:14,Amplifier:255,Duration:200,ShowParticles:0b},{Id:11,Amplifier:255,Duration:200,ShowParticles:0b}],Passengers:[{id:falling_block,Time:1,NoGravity:1b,BlockState:{Name:"minecraft:cobblestone"}}],Tags:["medievalorigins_rock_creeper"]}
execute at @s run tp @e[type=minecraft:creeper,tag=medievalorigins_rock_creeper] ~ ~ ~ facing ^ ^1 ^6
power grant @e[type=creeper,tag=medievalorigins_rock_creeper] medievalorigins:troll/rock_throw_properties_rock_throw_fall
power grant @e[type=creeper,tag=medievalorigins_rock_creeper] medievalorigins:troll/rock_throw_properties_rock_throw_timer
power grant @e[type=creeper,tag=medievalorigins_rock_creeper] medievalorigins:troll/rock_throw_properties_creeper_perm
power grant @e[type=creeper,tag=medievalorigins_rock_creeper] medievalorigins:troll/rock_throw_properties_rock_throw_velocity
power grant @e[type=creeper,tag=medievalorigins_rock_creeper] medievalorigins:troll/rock_throw_properties_rock_throw_path
power grant @e[type=creeper,tag=medievalorigins_rock_creeper] medievalorigins:troll/rock_throw_properties_creeper_noclip