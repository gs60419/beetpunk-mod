# Changelog

## Unreleased

- Changed pilgrim-book temple visits so they play seal feedback without granting formal temple certification advancements.
- Added one-time temple milestone performances for the first, fourth, twelfth, and thirteenth unique LV4 temple cores.
- Added low-frequency temple ambience: active cores now emit subtle theme-colored particles, with stronger range ambience and rare chimes at LV4.
- Changed temple core charge indicators into subtle side-face LED dots, leaving top and bottom faces unmarked.
- Added five visual activation states for all 13 temple cores, with LV1-LV4 shown as four scripture-charge indicator lights.
- Fixed vanilla crop planting on Beet Farmland and Fertilized Beet Farmland.
- Added first-pass temple world performances for scripture insertion, pilgrim-book sealing, all-13-seal completion, and revelation use.
- Added dedicated and universal crank-base barrel modes: same-type barrel stacks keep their speed bonus, while mixed barrel stacks can process recipes for each installed barrel type.
- Standalone prayer barrels now crank the entire connected barrel stack when used, even without a crank base.
- Fixed standalone stacked prayer barrels staying on the transparent spin-anchor model after the second or higher barrel stopped spinning.
- Moved the crank-base recipe hint side panel to the left side of the UI so it no longer overlaps JEI's item list.
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
