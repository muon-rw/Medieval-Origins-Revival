team join SirenSong @s
team join SirenSong @e[type=!minecraft:player,type=!#medievalorigins:bosses]
execute as @e[type=!#medievalorigins:bosses] at @s if entity @e[type=minecraft:player,limit=1,distance=..128] run data modify entity @s AngryTime set value 0