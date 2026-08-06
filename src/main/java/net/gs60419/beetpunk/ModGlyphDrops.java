package net.gs60419.beetpunk;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ModGlyphDrops {

    // ── 掉落機率 ────────────────────────────────────────────────────────────────
    private static final float SEED_GLYPH_CHANCE    = 0.03f;  // 播種（每顆種子 3%）
    private static final float SOIL_GLYPH_CHANCE    = 0.02f;  // 手摘收成（2%）
    private static final float SPROUT_GLYPH_CHANCE  = 0.02f;  // 手摘收成（暫時，待肥料完成後改）
    private static final float PIG_GLYPH_CHANCE     = 0.20f;  // 甜菜餵豬（20%）
    private static final float BEET_LV1_BONUS_CHANCE = 0.30f;
    private static final float LEAF_FIBER_CHANCE = 0.35f;

    private ModGlyphDrops() {}

    public static void register() {
        registerHarvestDrops();
        registerPlantingDrops();
        registerPigBreedDrops();
        Beetpunk.LOGGER.info("Registering Beetpunk glyph drops.");
    }

    // ── 收成觸發（沃土 SOIL、初芽 SPROUT） ────────────────────────────────────
    private static void registerHarvestDrops() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide() || !isSurvivalMatureBeetrootHarvest(player, state)) return;

            if (player instanceof ServerPlayer serverPlayer) {
                dropLeafTempleHarvest((ServerLevel) level, pos, serverPlayer);
                ModAdvancements.award(serverPlayer, ModAdvancements.FIRST_HAND_HARVEST, "hand_harvest");
                dropSoilGlyph(level, pos, serverPlayer);
                dropSproutGlyph(level, pos, serverPlayer);
                applyBeetTempleHarvest((ServerLevel) level, pos, serverPlayer);
            }
        });
    }

    private static void applyBeetTempleHarvest(ServerLevel level, BlockPos center, ServerPlayer player) {
        int templeLevel = TempleEffects.nearbyTempleLevel(level, center, ModBlocks.BEET_TEMPLE_CORE);
        if (templeLevel <= 0) {
            return;
        }

        if (level.getRandom().nextFloat() < BEET_LV1_BONUS_CHANCE) {
            Block.popResource(level, center, new ItemStack(Items.BEETROOT));
        }

        int radius = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 2 : templeLevel >= 2 ? 1 : 0;
        boolean replant = templeLevel >= 3;
        boolean collectToBox = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL;
        int harvested = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos target = center.offset(x, 0, z);
                if (target.equals(center)) {
                    if (replant) {
                        replant(target, player, harvested, templeLevel);
                    }
                    continue;
                }

                BlockState targetState = level.getBlockState(target);
                if (!isMatureBeetroot(targetState)) {
                    continue;
                }

                dropBeetHarvestOutput(level, target, new ItemStack(Items.BEETROOT), collectToBox);
                dropBeetHarvestOutput(level, target, new ItemStack(Items.BEETROOT_SEEDS, 1 + level.getRandom().nextInt(3)), collectToBox);
                dropLeafTempleHarvest(level, target, player);
                level.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
                harvested++;
                if (replant) {
                    replant(target, player, harvested, templeLevel);
                }
            }
        }
    }

    public static int harvestBeetrootsWithStaff(ServerLevel level, BlockPos center, ServerPlayer player) {
        int templeLevel = TempleEffects.nearbyTempleLevel(level, center, ModBlocks.BEET_TEMPLE_CORE);
        int radius = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 2 : templeLevel >= 2 ? 1 : 0;
        boolean replant = templeLevel >= 3;
        boolean collectToBox = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL;
        int harvested = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos target = center.offset(x, 0, z);
                if (!isMatureBeetroot(level.getBlockState(target))) {
                    continue;
                }

                dropBeetHarvestOutput(level, target, new ItemStack(Items.BEETROOT), collectToBox);
                dropBeetHarvestOutput(level, target, new ItemStack(Items.BEETROOT_SEEDS, 1 + level.getRandom().nextInt(3)), collectToBox);
                if (templeLevel > 0 && level.getRandom().nextFloat() < BEET_LV1_BONUS_CHANCE) {
                    dropBeetHarvestOutput(level, target, new ItemStack(Items.BEETROOT), collectToBox);
                }
                dropLeafTempleHarvest(level, target, player);
                dropSoilGlyph(level, target, player);
                dropSproutGlyph(level, target, player);
                level.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
                harvested++;
                if (replant) {
                    replant(target, player, harvested, templeLevel);
                }
            }
        }

        if (harvested > 0) {
            ModAdvancements.award(player, ModAdvancements.FIRST_HAND_HARVEST, "hand_harvest");
        }
        return harvested;
    }

    private static void dropBeetHarvestOutput(ServerLevel level, BlockPos pos, ItemStack stack, boolean collectToBox) {
        if (collectToBox && ModHarvestBoxes.tryInsertNearby(level, pos, stack)) {
            return;
        }
        Block.popResource(level, pos, stack);
    }

    private static void dropLeafTempleHarvest(ServerLevel level, BlockPos pos, ServerPlayer player) {
        int templeLevel = TempleEffects.nearbyTempleLevel(level, pos, ModBlocks.LEAF_TEMPLE_CORE);
        int leaves = 1;
        int fibers = 0;

        if (templeLevel >= 1) {
            leaves++;
        }
        if (templeLevel >= 2 && level.getRandom().nextFloat() < LEAF_FIBER_CHANCE) {
            fibers++;
        }
        if (templeLevel >= 3) {
            leaves++;
            fibers++;
        }

        giveOrDrop(level, pos, player, new ItemStack(ModItems.BEET_LEAF, leaves), templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL);
        if (fibers > 0) {
            giveOrDrop(level, pos, player, new ItemStack(ModItems.BEET_FIBER, fibers), templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL);
        }
    }

    private static void giveOrDrop(ServerLevel level, BlockPos pos, ServerPlayer player, ItemStack stack, boolean directToInventory) {
        if (stack.isEmpty()) {
            return;
        }
        if (directToInventory && ModHarvestBoxes.tryInsertNearby(level, pos, stack)) {
            return;
        }
        if (directToInventory && player.getInventory().add(stack)) {
            return;
        }
        Block.popResource(level, pos, stack);
    }

    private static void replant(BlockPos pos, ServerPlayer player, int harvested, int templeLevel) {
        ServerLevel level = player.level();
        if (!level.getBlockState(pos).isAir()) {
            return;
        }
        boolean freeSeed = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL && harvested > 0 && harvested % 4 == 0;
        if (!freeSeed && !consumeSeed(player)) {
            return;
        }
        level.setBlock(pos, Blocks.BEETROOTS.defaultBlockState(), 3);
    }

    private static void dropSoilGlyph(Level level, BlockPos pos, ServerPlayer player) {
        if (!ModAdvancements.isDone(player, BeetTempleTier.SEED.sealTempleAdvancementId())) return;
        if (level.getRandom().nextFloat() < SOIL_GLYPH_CHANCE) {
            Block.popResource(level, pos, new ItemStack(ModItems.SOIL_GLYPH));
        }
    }

    private static void dropSproutGlyph(Level level, BlockPos pos, ServerPlayer player) {
        if (!ModAdvancements.isDone(player, BeetTempleTier.SOIL.sealTempleAdvancementId())) return;
        if (level.getRandom().nextFloat() < SPROUT_GLYPH_CHANCE) {
            Block.popResource(level, pos, new ItemStack(ModItems.SPROUT_GLYPH));
        }
    }

    // ── 播種觸發（萌種 SEED） ─────────────────────────────────────────────────
    private static void registerPlantingDrops() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }

            ItemStack held = player.getItemInHand(hand);
            if (!held.is(Items.BEETROOT_SEEDS)) return InteractionResult.PASS;

            BlockPos farmlandPos = hitResult.getBlockPos();
            BlockState farmland = level.getBlockState(farmlandPos);
            if (!farmland.is(Blocks.FARMLAND)
                    && !farmland.is(ModBlocks.BEET_FARMLAND)
                    && !farmland.is(ModBlocks.FERTILIZED_BEET_FARMLAND)) {
                return InteractionResult.PASS;
            }

            // Block above must be empty (seed about to be planted)
            if (!level.getBlockState(farmlandPos.above()).isAir()) return InteractionResult.PASS;

            trySeedGlyphDrop(level, farmlandPos, serverPlayer);
            return InteractionResult.PASS;
        });
    }

    public static void trySeedGlyphDrop(Level level, BlockPos pos, ServerPlayer player) {
        if (level.getRandom().nextFloat() < SEED_GLYPH_CHANCE) {
            Block.popResource(level, pos, new ItemStack(ModItems.SEED_GLYPH));
        }
    }

    private static boolean consumeSeed(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }

        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(Items.BEETROOT_SEEDS)) {
                stack.shrink(1);
                inventory.setChanged();
                return true;
            }
        }
        return false;
    }

    // ── 餵豬觸發（聖豬 PIG） ─────────────────────────────────────────────────
    private static void registerPigBreedDrops() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }
            if (!(entity instanceof Pig pig) || pig.isBaby()) return InteractionResult.PASS;
            if (!player.getItemInHand(hand).is(Items.BEETROOT)) return InteractionResult.PASS;
            if (!pig.canFallInLove()) return InteractionResult.PASS;
            if (!ModAdvancements.isDone(serverPlayer, BeetTempleTier.DYE.sealTempleAdvancementId())) {
                return InteractionResult.PASS;
            }

            int templeLevel = TempleEffects.nearbyTempleLevel(serverPlayer.level(), pig.blockPosition(), ModBlocks.PIG_TEMPLE_CORE);
            if (level.getRandom().nextFloat() < pigGlyphChance(templeLevel)) {
                Block.popResource(level, pig.blockPosition(), new ItemStack(ModItems.PIG_GLYPH));
            }
            if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
                spawnPiglet(serverPlayer.level(), pig.blockPosition());
            }
            return InteractionResult.PASS;
        });
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────
    private static float pigGlyphChance(int templeLevel) {
        if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
            return 0.75f;
        }
        if (templeLevel >= 3) {
            return 0.50f;
        }
        if (templeLevel >= 2) {
            return 0.35f;
        }
        return PIG_GLYPH_CHANCE;
    }

    private static void spawnPiglet(ServerLevel level, BlockPos pos) {
        Pig piglet = (Pig) BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace("pig")).create(level, EntitySpawnReason.MOB_SUMMONED);
        if (piglet == null) {
            return;
        }
        piglet.setBaby(true);
        piglet.snapTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.0D,
                pos.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F,
                0.0F);
        level.addFreshEntity(piglet);
    }

    private static boolean isSurvivalMatureBeetrootHarvest(Player player, BlockState state) {
        return !player.getAbilities().instabuild
                && isMatureBeetroot(state);
    }

    private static boolean isMatureBeetroot(BlockState state) {
        return state.is(Blocks.BEETROOTS)
                && state.getValue(BeetrootBlock.AGE) >= BeetrootBlock.MAX_AGE;
    }
}
