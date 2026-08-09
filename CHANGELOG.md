# Changelog

## Unreleased

- Added Beet on a Stick as a beetroot-themed pig-riding controller, including item texture, recipe, localization, creative tab entry, and pig control support.
- Added a crank-base recipe hint side panel that switches between extractor, grinder, and washing recipes based on the barrel installed above the base.
- Restored pilgrim book goshuin pages to use the prepared textured totem layers instead of clean geometric placeholder marks.
- Added five Pilgrim Staff appearance stages driven by the holding player's temple seal progress.
- Added Beet Pilgrim Staff harvesting: right-click mature beetroot crops to harvest them, with Beet Temple range, replanting, and harvest-box support at higher activation levels.
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
