## 7.0.3-alpha-9
- Fix Dwarves sometimes not sinking in water
- Fix a gradual memory leak when worlds were left open for a very long time

## 7.0.3-alpha-8
- Fix Revenant and Pixie being able to bypass trading restrictions using a Trading Post
- Fix Fae not having reduced health (for realsies this time)
- Fix Banshee Hexed not working at all
- Fix thermoo patches compat powers resetting on death 
- Ice and Fire compat with Sirens should now be a little bit more stable. 

## 7.0.3-alpha-7
- Fix Incubus being unable to fight the Wither, and being immune to poison/regen
- Banshees are now considered Undead

## 7.0.3-alpha-6
- Fix Incubus' Demon Fire and High Elf's Ebon/Blazenbreath frequently not damaging the primary target, only surrounding entities
- Fix Gorgons being able to jump while petrified
- Revenants and Plague Victims are no longer ignored by the Wither, or able to breathe infinitely underwater
- Fix Dwarves not sinking in water
- Fix Guards and Iron Golems not attacking Revenants
- Revenant summons will now teleport to the player when set to follow, if they get stuck too far away.

## 7.0.3-alpha-5
- Update to support IceAndFire-CE 2.0
- Add Power Cache for power lookups in Origins, should reduce CPU server load from this mod by a huge amount
- Banshee sonic shriek now scales with soul spell power and properly attributes damage dealt to the player
- Autodetect epic knights weapons for Valkyrie
- Add workaround for an Origins bug causing attribute changes to reset on death
- Add workaround for an Origins bug causing empowered shot to sometimes get softlocked at full charge value
- Removed Yeti

## 7.0.3-alpha-4
- Improve Nature's Blessing particle effects
- Fix Nature's Blessing not clearing negative effects
- Improve logic of allied checks for cases of other player's tamed pets
- Fix one last check that improperly referenced FTB Teams data on the client-side
- Added keybind info to Revenant's summoning power 

## 7.0.3-alpha-3
- Fix Fae heal crashing if no mod providing `spell_power:earth` was installed

## 7.0.3-alpha-2
- Fix Wood Elf's **Focus** power not working. Also now compatible with other arrow-modifying mods 
- Change "Notable Changes" in origin descriptions to "Overview"
- Re-add High Elf's Instant Cure power

## 7.0.3-alpha-1
- **Fae Rewrite**!
  - *Players with the Fae Origin will need to reselect their origin to receive any of the new powers.*
  - **Overall Role**: Support, Nature Magic, Protector.
  - **Kept Abilities**:
    - `Levitation`: Active upward flight.
    - `Allure`: Increased mob detection range.
    - `Diminutive`: Small size and adjusted speed.
    - `Iron Aversion`: Cannot wear iron/silver.
    - `Reduced Health`: Lower base health.
  - **New/Reworked Abilities**:
    - `Wings`: Custom wings, animated while jumping or flying, similar to Pixie wings (currently the same texture).
    - `Nature's Blessing` (Tertiary Active): Activates a temporary surge of nature magic.
      - During the surge:
        - Automatically plants seeds from a defined tag in a small radius around the Fae while moving.
        - Periodically pulses a heal to nearby players and cleanses negative effects from self and allies.
        - Accompanied by visual particle effects.
    - `Nourishment` (Secondary Active): Unleashes a burst of life energy, instantly bonemealing plants in an area.
    - `Forest Kin`: Grants passive health regeneration while in forest biomes.
    - `Nature's Ward`: Provides immunity to poison and nausea, and resistance to magic damage.
- Fix Revenant summons despawning in Peaceful difficulty
- Allow Revenant summons to be equipped with armor 
- Revenant's command power hotkey no longer requires sneaking
- Fixed Pixie hitbox
- Fixed the size of Yeti

## 7.0.2-alpha-4
- Properly fix FTB Teams client-side checks. 

## 7.0.2-alpha-3
- Fix a rare crash caused by FTB Teams integration in the Medieval MC modpack

## 7.0.2-alpha-2
- Fix mixin crash with Ice and Fire outside of dev

## 7.0.2-alpha-1
- Mirroring and Featherweight are no longer treasure-only enchantments
- Add integration for Ice and Fire: Community Edition:
- The Mirroring Enchantment now works on Gorgons from Ice and Fire
- Sirens will no longer be able to charm Siren players
- Pixies will no longer steal from Pixie players
- Added mild heat resistance to Dwarf and Alfiq (with Thermoo Patches)

## 7.0.1-alpha-6
- Added a tiny grace period after triggering Intervention where Valkyries still have fall immunity 
- Fixed Revenant's Black Thumb not working
- Added a usability hint to Black Thumb

## 7.0.1-alpha-5
- Fix compatibility with new versions of Icarus
- Fixed a crash caused by Revenant's using Hellraiser

## 7.0.2-alpha-2/3
- Removed unused Spell Engine compat. Allowed any version of spell engine. 

## 7.0.1-alpha-1
- Add temporary patch for Origins alpha bug - fixes attributes/sizes resetting on respawn

## 7.0.0-alpha-9
- Changed Alfiq's On Your Feet - now applies if the player is looking straight down when landing, instead of based on fall distance. 
- Added dust particles to the leap section of Alfiq's Pounce

## 7.0.0-alpha-8
- Made Incubi a bit less red

## 7.0.0-alpha-7
- Cleaned up the description of Revenant's Revenance power, split into badges
- Revenants now restore a small amount of hunger when eating bones or heads
- Fixed bones and heads not being edible if at full hunger
- Removed reference to nonexistent Instant Cure power
- Clarify spell engine dependency in fabric.mod.json, restrict from upcoming version

## 7.0.0-alpha-6
- Fix Incubus' Unholy Deal not dealing any damage to the player

## 7.0.0-alpha-5
- Add FTB Teams support to Valkyrie intervention/divine smite targeting logic
- Add FTB Teams support to Revenant summons targeting
- Fixed the badge tip for Pixie's flight effects toggle showing the wrong key
- Alfiq can now charge Pounce while moving
- Alfiq meows are now louder and more spammable :)
- Alfiq player pickpocketing now has a cooldown
- Made Goblins significantly less green, and Moon Elves a little less blue
- Revenant Black Thumb should now work on most modded crop blocks automatically
- Fixed the pickpocket cooldown bar always showing

## 7.0.0-alpha-4
- Compatible with Origins alpha.12
- Wood Elf covered-stealth is now a bit more "stealthy" (since it's now harder to apply)

## 7.0.0-alpha-3
- Fix untranslated usability hints in tooltips
- Fix Valkyrie Intervention heal not scaling based off of healing spell power
- Intervention now pushes back non-allies when applying
- Fix Revenant's soul spell power based summon scaling

## 7.0.0-alpha-2
- Fix Moon/Wood Elf stealths not affecting mobs
- Fix Wood Elf stealth being triggerable by crouching in between short crops 
- Fix Faes not having increased mob allure
- Fix Banshees being targeted by mobs during Spectral
- Fix Pixie particles not rendering
- Fix Valkyrie's Intervention ability not being usable
- Intervention will now always end after 10 seconds if not used.

## 7.0.0-alpha-1
*Initial alpha port to 1.21.1*


**There will be bugs.**
**Back up your worlds before installing, and frequently while playing!**

- No longer bundles Apugli or requires Pehkui - performance should be significantly better!
- Now Fabric only - when Origins Fabric is out of alpha and Sinytra Connector updates, I'll contribute to improving their compatibility wherever possible
- Valkyries now get extra Vanquisher points for slaying undead bosses
- High Elf's Glacial Step now increases level of Frost Walker
- Added a keybind "Tertiary Active" for pixie effects and the new revenant command power.
- "Golden Armor" now also includes gold-trimmed armor
- Revenants can now command their summons to "sit", or teleport all owned summons to them
- Revenants can now eat bones/heads even at full hunger
- Revenants are now limited to 5 summons of any type at a time
- Revenant summon kills now grant EXP and kill credit
- Revenant summons will now always drop any items you give them
- Revenant skeletons will now no longer be targeted by tamed wolves
- Revenant's combat summons now expire (5 minutes for zombies, 10 for skeletons, 30 for wither skeletons), added a duration bar 
- Putrid Communion now increases the *duration* of combat summons
- *Attributes* of combat summons are now only determined by Soul Spell Power
- Fixed some death messages being untranslated
- Fixed a crash when a Valkyrie cleared multiple negative effects from another player at once
- Fixed spell damage powers crashing on dedicated servers in rare mod compatibility cases 