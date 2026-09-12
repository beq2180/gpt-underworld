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
