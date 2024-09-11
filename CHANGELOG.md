## 6.4.6
- Fixed compatibility with Mermod 3.0.1+
- Fixed mana inconsistencies with the `cast_spell` entity action with Iron's
- Added a few new fields to the cast spell entity action, see the `test` power for an example
- Fixed a potential crash when Valkyries land near a player with negative effects
- Siren Song will no longer add particles to players

## 6.4.5
- Added compatibility for Mermod on Forge (should be compatible with any existing Fabric datapacks)
- Fix startup crash with Serious Player Animations

## 6.4.4
- Added sounds and effects to Sirens' Song
- Increased the radius and duration of Sirens' Song, but increased its cooldown
- (Forge) Fixed Siren Song not pacifying already aggressive mobs
- Cleaned up/shortened a few more power descriptions
- Fixed siren song particles sometimes never despawning
- Reduced the hunger cost of Valkyrie flight by 75%

## 6.4.3
- Fixed Pixie's hover being unusable when relogging or first selecting the origin
- Made pixie's flight resource regenerate a bit slower (for balance)
- Fixed an incorrect dependency on Curseforge (Forge versions were being directed to install Origins Fabric)

## 6.4.2
- Fixed Arachae, Alfiq, Moon Elves, and High Elves having 3 fewer hearts than before
- Updated Korean translation thanks to Colddo118 (other translations are also out of date - if you'd like to contribute, please do so on Github!)
- Fixed Valkyrie's divine smite being too quiet

## 6.4.1
- Added Tridents to Valkyrie's Heavenly Implements and automatically support many more modded items 

## 6.4.0 
**Initial release of a large project restructure. There may be bugs!**
- Merged projects to cross-platform (Fabric and Forge/Neoforge will now update simultaneously!)
- Added a new Icarus-mod based origin, the Valkyrie
- Fixed an issue where Pixies would sometimes retain their flight power after switching origins
- Revenant summons will now move a bit more naturally and try harder to avoid water
- Fixed Wood Elf's Focus power being mistranslated
- Elves, Arachnae, and Alfiq are now slowed by heavy armor instead of being unable to equip it
- Heavy armor is now determined by armor toughness
- Added pixie wing animations
- Remade pixie flight to be easier to use, but operate based on a limited resource
- Made pixie's mischief power play sounds less often
- Made Icarus compat more friendly with other mods (thanks Up!)
- Made Revenant's siphon resource more visible
- Changed the Gorgon icon
- (Forge) Fixed some Goblin gear bonuses not working
- (Fabric) Added support for RPGMana to High Elf's Highborne power
- Internal only, note for Origins devs - added a `medievalorigins:cast_spell` entity action, which is integrated with Iron's Spells (Forge) and Spell Engine (Fabric). 
You can find examples for these in `(fabric/forge)/src/main/resources/data/medievalorigins/powers/test.json`
- The format may change slightly if features are added.

## 6.3.10
- Lowered Fae attraction range from 2048 blocks to 64 (woops)
- Fixed Fae's Magical Suppression only checking for Iron and not Silver. 

## 6.3.9
- Improved Simply Swords compatibility with Moon Elf's Moonlight Rogue power
- Added 2 new powers to Banshee, Cursed Sigil and Cursed Ward
- Added Origins' Like Water power to Siren
- Alfiq can now jump more often and charge their jump more quickly
- Lowered Alfiq's On Your Feet damage threshold from 10 health to 6
- Added Russian Translation (thanks UFOS228)
- Shortened some English descriptions
- Rewrote most of Siren's underwater bonuses to no longer use status effects (thanks lukaskek!)
- Improved Pixie flight compatibility with Creative flight (pixie flight itself unchanged for now)
- Fixed Alfiq jump boost sometimes not working
- Fixed High Elves sometimes receiving two sets of starter items

## 6.3.8
- Added more compatibility to Ogre's Ravager passive

## 6.3.7
- Fixed pixies not being able to use Quartz Elevators

## 6.3.6
- Fixed log spam from pixie flight power during rain

## 6.3.5
- Added optional compat with Frostiful/Scorchful (requires Thermoo Patches)

## 6.3.4
- Updated Goblin's Greedy power for the latest version of AEA

## 6.3.3
- Removed Icarae (Now has its own official addon: https://modrinth.com/mod/icarae-origin)

## 6.3.2
- Update for Icarus 2.6.0

## 6.3.1
- Updated Icarus compat
- Updated Spell Power compat
- Fix RPGDifficulty incompat (revenant zombies being unsummonable)
- Bundled Projectile Damage (fixes revenant summons being unsummonable if you didn't have it installed)
- Fixed a crash related to Alfiq's pickpocket
- Fixed Fae attraction range remaining after switching origins
- Fixed Yetis not being immune to vanilla freezing damage

## 6.3.0
- Added (with permission) the **Icarae**, birdlike carnivorous hunters, an Icarus mod exclusive origin
- High Elf, Incubus, and Revenant abilities now scale with Spell Power
- Fixed Revenant's custom aggro breaking vanilla teams
- Fixed Siren Song causing non-hostile entities to become aggressive
- Fixed Siren Song creating server lag
- Fixed magic-based damage changes and damage types not working as intended
- Switched from custom item conditions to registering with AutoTag API (should improve server performance)
- Added some barebones internals for other datapack developers to create functional Icarus origins or spell-power scaling abilities of their own
- The Featherweight enchantment will now also prevent enchanted armor from slowing down Icarus wing flight, and armor calculations will be based on equipped armor pieces, not innate player stats.

## 6.2.4 (Alpha)
#### New Origins:
- Added **High Elf**, a magic based origin that currently doesn't have any integration with the RPG Series/Spell Engine, but will soon!
- Added **Alfiq** - a catlike species who excel at hunting. They can charge up to pounce, climb walls, and deal extra damage with their claws.
- 75% of a secret third heavily requested origin, currently disabled
#### Major reworks:


- **Banshee**, including a protective shriek, a replacement for diet, and unique ghostly flight.
- Necromancer, renamed to **Revenant**, an undead who can raise summoned undead which they can equip with weapons, and siphon the life force from enemies.
- **Pixie** "flight" changed, with tiny little wings, can sit on other players' heads, and chestplate restriction changed to allow leather
#### Other changes:
- Dwarves can now break anything that requires a wood pickaxe
- Improved aggro logic for when a Revenant hits an Illager - all nearby illagers will become aggressive until dying or relogging
- Changed Dwarf to Impact 2 (just for accuracy)
- Reworked Revenant's Illager allying - should work with all modded pillagers, and they will now attack back if you hit them
- Added built-in water protection when wearing a full set of some modded armors (such as the Diving Suit from Alex's Caves)
- All modded daggers and similar items should receive bonuses for Moon Elves automatically
- All modded golden and gilded items should receive bonuses for Goblins automatically
- Reworked and slightly buffed how Goblin's Greedy passive works
- Made Incubi stronger while in the Nether
- Gave Arachnae and Siren a few powers from base Origins, as buffs
- Made Goblins less green
- Fixed several tooltip issues
- Fixed many damage modifiers not working
- Made Featherweight and Mirroring slightly easier to get
- Major changes to pixie flight, make sure to read the new description!
- Banshee Shriek now pushes back enemies a meaningful distance
- Increased the cooldown of Banshee Spectral from 30 seconds to 45
- Added a few more mod daggers to Moon Elf's bonus
- Fixed minor glitches in Moon Elf dagger tooltip
- Effects should never be cleared unnecessarily
- Gorgon gaze can now affect players, preventable with a new enchant Mirroring
- Dwarves can apply Mirroring to tolerate sunlight with any helmet
- Dwarf night vision is now toggleable
- Dwarves fit in 1-block tunnels again
- Goblins now have increased durability with gold gear
- Wood Elf's improved vision now instead activates while crouching and covered by foliage, while also making them harder to detect by mobs.
- Temporary killed Wood Elf's piercing shot (Focus) ability, pending a bugfix. Buffed arrow damage to compensate
- Moon Elves are now slightly better with daggers, and no longer get attacked by phantoms
- Ogre is now larger (intentional nerf, the origin was a bit too strong)
- Elves are now a little bit taller
- Various damage boosts/resistances/vulnerabilities which previously did not work should be functioning properly again.
- Pixies should be able to see in first person while riding things.
- Other minor fixes that got lost in commit-note hell.