# Changelog

## Unreleased

- Removed the legacy single-block processing tables:
  - `beet_processing_table`
  - `beet_grinder_table`
  - `beet_washing_table`
- The intended processing route is now `beet_crank_base` plus one or more matching prayer barrels:
  - `beet_extractor_barrel`
  - `beet_grinder_barrel`
  - `beet_washing_barrel`
- Updated barrel container titles to use barrel names instead of table names.
- Removed legacy table recipes, recipe advancements, loot tables, blockstates, item models, and unused grinder-table textures.

## 0.1.0

- Initial public Beetpunk prototype for Minecraft Java 26.2 / Fabric.
- Added the beetroot material cycle, prayer barrel processing, 13 temples, glyphs, scriptures, pilgrim book, pilgrim staff, beet wood/stone/iron routes, beet villager trading, sprinkler, harvest box, and geothermal lava support.
