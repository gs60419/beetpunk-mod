package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BeetPilgrimStaffItem extends Item {
	private static final int BASE_SEED_RADIUS = 1;

	public BeetPilgrimStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}

		BlockPos pos = context.getClickedPos();
		if (canTransformSoil(level, pos)) {
			return transformSoil(context);
		}
		if (isPlantableSoil(level.getBlockState(pos))) {
			return plantSeeds(context);
		}
		if (level.getBlockState(pos).is(Blocks.BEETROOTS)) {
			return isMatureBeetroot(level.getBlockState(pos)) ? harvestBeetroot(context) : growBeetroot(context);
		}
		return InteractionResult.PASS;
	}

	private static InteractionResult plantSeeds(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos center = context.getClickedPos();
		if (player == null) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		int planted = plantArea((ServerLevel) level, player, center);
		if (planted == 0) {
			return InteractionResult.FAIL;
		}

		level.playSound(null, center, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.8F, 1.0F);
		if (player instanceof ServerPlayer serverPlayer) {
			ModAdvancements.award(serverPlayer, ModAdvancements.USE_PILGRIM_STAFF, "used");
		}
		return InteractionResult.SUCCESS;
	}

	private static int plantArea(ServerLevel level, Player player, BlockPos center) {
		int templeLevel = TempleEffects.nearbyTempleLevel(level, center, ModBlocks.SEED_TEMPLE_CORE);
		int radius = Math.max(BASE_SEED_RADIUS, templeLevel);
		int planted = 0;
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				BlockPos farmlandPos = center.offset(x, 0, z);
				BlockPos cropPos = farmlandPos.above();
				boolean freeSeed = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL && planted > 0 && planted % 4 == 0;
				if (!canPlantAt(level, farmlandPos, cropPos) || (!freeSeed && !consumeSeed(player))) {
					continue;
				}

				level.setBlock(cropPos, Blocks.BEETROOTS.defaultBlockState(), 3);
				planted++;
				if (player instanceof ServerPlayer serverPlayer) {
					ModGlyphDrops.trySeedGlyphDrop(level, farmlandPos, serverPlayer);
				}
			}
		}
		return planted;
	}

	private static InteractionResult transformSoil(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos center = context.getClickedPos();
		if (player == null) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		int transformed = transformArea((ServerLevel) level, center, player);
		if (transformed == 0) {
			return InteractionResult.FAIL;
		}

		if (player instanceof ServerPlayer serverPlayer) {
			ModAdvancements.award(serverPlayer, ModAdvancements.EMPOWER_SOIL, "empowered");
		}
		level.playSound(null, center, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.8F, 0.8F);
		return InteractionResult.SUCCESS;
	}

	private static int transformArea(ServerLevel level, BlockPos center, Player player) {
		int templeLevel = TempleEffects.nearbyTempleLevel(level, center, ModBlocks.SOIL_TEMPLE_CORE);
		int radius = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 2 : templeLevel >= 2 ? 1 : 0;
		boolean fertilize = templeLevel >= 3;
		int transformed = 0;

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				BlockPos target = center.offset(x, 0, z);
				BlockState newState = transformedState(level, target, fertilize);
				if (newState == null) {
					continue;
				}
				if (!consumeSoilOffering(player)) {
					return transformed;
				}
				level.setBlock(target, newState, 3);
				transformed++;
			}
		}
		return transformed;
	}

	private static InteractionResult harvestBeetroot(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		if (player == null) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.FAIL;
		}

		int harvested = ModGlyphDrops.harvestBeetrootsWithStaff((ServerLevel) level, pos, serverPlayer);
		if (harvested == 0) {
			return InteractionResult.FAIL;
		}

		level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
		return InteractionResult.SUCCESS;
	}

	private static InteractionResult growBeetroot(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		ServerLevel serverLevel = (ServerLevel) level;
		int templeLevel = TempleEffects.nearbyTempleLevel(serverLevel, pos, ModBlocks.GROWTH_TEMPLE_CORE);
		if (growArea(serverLevel, pos, templeLevel) == 0) {
			return InteractionResult.FAIL;
		}

		level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 0.9F, 1.15F);
		return InteractionResult.SUCCESS;
	}

	private static int growArea(ServerLevel level, BlockPos center, int templeLevel) {
		int radius = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 2 : templeLevel >= 3 ? 1 : 0;
		int steps = templeLevel >= 2 ? 2 : 1;
		int grown = 0;
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (growBeetroot(level, center.offset(x, 0, z), steps)) {
					grown++;
				}
			}
		}
		return grown;
	}

	private static boolean isMatureBeetroot(BlockState state) {
		return state.is(Blocks.BEETROOTS)
				&& state.getValue(BeetrootBlock.AGE) >= BeetrootBlock.MAX_AGE;
	}

	private static boolean growBeetroot(ServerLevel level, BlockPos pos, int steps) {
		BlockState state = level.getBlockState(pos);
		if (!state.is(Blocks.BEETROOTS)) {
			return false;
		}

		int age = state.getValue(BeetrootBlock.AGE);
		if (age >= BeetrootBlock.MAX_AGE) {
			return false;
		}

		level.setBlock(pos, state.setValue(BeetrootBlock.AGE, Math.min(BeetrootBlock.MAX_AGE, age + steps)), 3);
		return true;
	}

	private static boolean canPlantAt(Level level, BlockPos farmlandPos, BlockPos cropPos) {
		return isPlantableSoil(level.getBlockState(farmlandPos)) && level.getBlockState(cropPos).isAir();
	}

	private static boolean isPlantableSoil(BlockState state) {
		return state.is(Blocks.FARMLAND)
				|| state.is(ModBlocks.BEET_FARMLAND)
				|| state.is(ModBlocks.FERTILIZED_BEET_FARMLAND);
	}

	private static boolean canTransformSoil(Level level, BlockPos pos) {
		return transformedState(level, pos, false) != null || transformedState(level, pos, true) != null;
	}

	private static BlockState transformedState(Level level, BlockPos pos, boolean fertilize) {
		if (!level.getBlockState(pos.above()).isAir()) {
			return null;
		}

		BlockState state = level.getBlockState(pos);
		if (state.is(Blocks.FARMLAND)) {
			return farmlandState(fertilize, state.getValue(FarmlandBlock.MOISTURE));
		}
		if (state.is(ModBlocks.BEET_FARMLAND)) {
			return fertilize ? farmlandState(true, state.getValue(FarmlandBlock.MOISTURE)) : null;
		}
		if (state.is(ModBlocks.FERTILIZED_BEET_FARMLAND)) {
			return null;
		}
		if (state.is(ModBlocks.FERTILIZED_BEET_SOIL)) {
			return farmlandState(true, FarmlandBlock.MAX_MOISTURE);
		}
		if (state.is(ModBlocks.BEET_SOIL)) {
			return farmlandState(fertilize, FarmlandBlock.MAX_MOISTURE);
		}
		if (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT) || state.is(Blocks.PODZOL)) {
			return farmlandState(fertilize, FarmlandBlock.MAX_MOISTURE);
		}
		return null;
	}

	private static BlockState farmlandState(boolean fertilize, int moisture) {
		return (fertilize ? ModBlocks.FERTILIZED_BEET_FARMLAND : ModBlocks.BEET_FARMLAND)
				.defaultBlockState()
				.setValue(FarmlandBlock.MOISTURE, Math.min(moisture, FarmlandBlock.MAX_MOISTURE));
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

	private static boolean consumeSoilOffering(Player player) {
		if (player.getAbilities().instabuild) {
			return true;
		}

		Inventory inventory = player.getInventory();
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (stack.is(ModItems.BEET_RESIDUE) || stack.is(ModBlocks.BEET_SOIL.asItem())) {
				stack.shrink(1);
				inventory.setChanged();
				return true;
			}
		}
		return false;
	}
}
