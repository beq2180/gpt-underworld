# The Underworld — Fabric 1.21.11

First iteration of the custom **Underworld** dimension.

## What is implemented

- New `Undium` block.
- Rare Undium clumps in **Nether Wastes** and **Basalt Deltas**.
- Nether-shaped portals made from Undium.
- Light an Undium frame with flint and steel in the **Overworld**.
- Two-way travel between Overworld and `underworld:the_underworld`.
- Three custom biomes:
  - **Ashlands**
    - Groil surface.
    - Cobbled Deepslate terrain.
    - Calcite pockets.
    - Sparse dark oak trees (~0.2 attempts/chunk).
  - **Trunks**
    - Groil surface.
    - Cobbled Deepslate terrain.
    - Dense spruce trees (~5 attempts/chunk).
  - **Islands**
    - Floating Deepslate/Cobbled Deepslate island masses.
    - Large islands plus smaller satellite islands.
- Deepslate ore pockets in all three Underworld biomes:
  coal, iron, copper, gold, redstone, lapis, diamond, emerald.
- Placeholder architecture ready for structures in the next iteration.

## Build

Requires **Java 21**.

macOS / Linux:

```bash
./gradlew build
```

Windows:

```bat
gradlew.bat build
```

The mod JAR is created in:

`build/libs/underworld-0.1.0.jar`

Install it together with Fabric Loader and Fabric API for Minecraft 1.21.11.

## Notes

This iteration deliberately keeps structures out of worldgen so they can be
added cleanly next without entangling the biome/terrain code.

The custom terrain passes run during world generation. Existing chunks are not
retrofitted.


## v0.2 structures

The Underworld now includes three data-driven structures:

- **Underworld Village** — deepslate/dark-oak settlement inspired by a plains village.
- **Small Dungeon** — underground deepslate room with zombie/skeleton spawners and basic loot chests.
- **Mining Chasm** — a deep open excavation shaft with a broken, non-functional redstone mining machine at the bottom.

Test with `/locate structure underworld:underworld_village`, `/locate structure underworld:small_dungeon`, and `/locate structure underworld:mining_chasm`.


## v0.2.1 roof fix

Fixed Underworld Village roofs so dark-oak stair gables slope inward correctly instead of expanding sideways into long wooden ribs.


## v0.2.2
- Added a per-player 20-tick (1 second) portal grace period after dimension travel so players are not immediately teleported back while standing in the destination portal.

## v0.3.0 - Gloomgrazer
- Adds the friendly Gloomgrazer passive mob.
- Cow-like wandering and wheat breeding.
- 15 HP.
- Adult Gloomgrazers can be bottled with a glass bottle to obtain Dragon's Breath.
- Spawns naturally in Ashlands, Trunks, and Islands.
- Quick test: `/summon underworld:gloomgrazer`.

## v0.3.2
- Gloomgrazer natural spawning is restricted to Ashlands and Trunks (not Islands).
- Gloomgrazers use a dark-dimension-safe custom spawn predicate on Groil, so they do not require vanilla animal skylight.
- Added custom synthesized Gloomgrazer ambient, hurt, death, and bottle-milking sounds.
