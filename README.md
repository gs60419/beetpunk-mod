# Beetpunk

Fabric prototype for the Beetpunk Minecraft mod concept.

## Website

The visual guide and interactive crafting tree are available at:

https://gs60419.github.io/beetpunk-mod/

## Current Scope

- Minecraft Java 26.2
- Fabric Loader 0.19.3
- Fabric API 0.154.0+26.2
- Java 25
- Mod ID: `beetpunk`

The current MVP centers on a beetroot-based skyblock material cycle:

- Mature vanilla beetroot crops drop the original beetroot plus Beetpunk byproducts such as beet leaves.
- Beet leaves lead into fiber, cloth, sticks, wood-like blocks, beds, signs, boats, and tool recipes.
- Beet blocks dehydrate into the stone route, then branch into cobblestone, gravel, sand, soil, iron dust, redstone dust, crystal grain, clay, and dripstone materials.
- Beet oil, beet water drops, beet water, filters, residue, and fertilizer support the extractor, grinder, washing table, sprinkler, and soil upgrades.
- Beet iron ingots support the beet-iron tool and armor route.
- The pilgrim staff handles the merged utility-tool path for planting, soil transformation, and growth support.
- The pilgrim book is the combined route guide and seal book.
- All 13 temple cores, glyphs, and 4 scripture levels are registered.
- Temple cores use scripture books for activation levels and the pilgrim book for seals.
- The crank base plus extractor, grinder, and washing prayer barrels form the preferred processing system; same-type stacked barrels speed up processing.
- The light temple can unlock a geothermal core that slowly fills cauldrons with lava when paired with pointed dripstone.
- A Beet Sage villager profession uses the beet trading table and buys beetroot or Beetpunk materials for the skyblock economy.

## Workstations And Machines

- `beet_crank_base`: main processing UI and power base for prayer barrels.
- `beet_extractor_barrel`: extractor route for liquids and residue.
- `beet_grinder_barrel`: physical stone/mineral breakdown route.
- `beet_washing_barrel`: filter-and-water washing route.
- `beet_sprinkler`: consumes beet water for crop support.
- `beet_harvest_box`: stores automated harvest output.
- `beet_trading_table`: workstation for the Beet Sage villager.
- `beet_geothermal_core`: late-game light-temple lava support block.

## Build

```powershell
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-25.0.3.9-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build --no-daemon
```

The compiled jar is written to:

```text
build/libs/beetpunk-0.1.0.jar
```

## Design Notes

The project notes live under:

```text
DEVELOPMENT_LOG.md
BEETPUNK_ROUTE_GUIDE.md
CHANGELOG.md
```
