package net.gs60419.beetpunk;

import java.util.HashSet;
import java.util.Set;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class ModSproutTempleEffects {
	private static final int CHECK_INTERVAL_TICKS = 200;
	private static final int CORE_SEARCH_RADIUS = 64;
	private static int ticks;

	private ModSproutTempleEffects() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ticks++;
			if (ticks < CHECK_INTERVAL_TICKS) {
				return;
			}
			ticks = 0;

			for (ServerLevel level : server.getAllLevels()) {
				Set<BlockPos> cores = findNearbySproutCores(level);
				for (BlockPos corePos : cores) {
					applySproutField(level, corePos);
				}
			}
		});
		Beetpunk.LOGGER.info("Registering Beetpunk sprout temple effects.");
	}

	private static Set<BlockPos> findNearbySproutCores(ServerLevel level) {
		Set<BlockPos> cores = new HashSet<>();
		level.players().forEach(player -> {
			BlockPos origin = player.blockPosition();
			for (BlockPos target : BlockPos.betweenClosed(
					origin.offset(-CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS),
					origin.offset(CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS))) {
				if (level.getBlockState(target).is(ModBlocks.SPROUT_TEMPLE_CORE)
						&& BeetTempleCoreBlock.templeLevel(level, target) > 0) {
					cores.add(target.immutable());
				}
			}
		});
		return cores;
	}

	private static void applySproutField(ServerLevel level, BlockPos corePos) {
		int templeLevel = BeetTempleCoreBlock.templeLevel(level, corePos);
		int range = TempleEffects.templeRange(templeLevel);
		if (range <= 0) {
			return;
		}

		int attempts = switch (templeLevel) {
			case 1 -> 24;
			case 2 -> 48;
			case 3 -> 80;
			default -> 120;
		};

		for (int attempt = 0; attempt < attempts; attempt++) {
			BlockPos cropPos = randomPosAround(level, corePos, range);
			BlockState state = level.getBlockState(cropPos);
			if (!isYoungBeetroot(state)) {
				continue;
			}

			if (templeLevel >= 1 && level.getRandom().nextFloat() < 0.35F) {
				advanceFirstSprout(level, cropPos, state);
			}
			if (templeLevel >= 2) {
				spreadSprout(level, cropPos, templeLevel);
			}
		}
	}

	private static BlockPos randomPosAround(ServerLevel level, BlockPos center, int range) {
		int x = center.getX() + level.getRandom().nextInt(range * 2 + 1) - range;
		int y = center.getY() + level.getRandom().nextInt(range * 2 + 1) - range;
		int z = center.getZ() + level.getRandom().nextInt(range * 2 + 1) - range;
		return new BlockPos(x, y, z);
	}

	private static boolean isYoungBeetroot(BlockState state) {
		return state.is(Blocks.BEETROOTS) && state.getValue(BeetrootBlock.AGE) <= 1;
	}

	private static void advanceFirstSprout(ServerLevel level, BlockPos pos, BlockState state) {
		int age = state.getValue(BeetrootBlock.AGE);
		if (age < 1) {
			level.setBlock(pos, state.setValue(BeetrootBlock.AGE, age + 1), 3);
		}
	}

	private static void spreadSprout(ServerLevel level, BlockPos cropPos, int templeLevel) {
		int attempts = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 2 : 1;
		boolean diagonals = templeLevel >= 3;
		for (int attempt = 0; attempt < attempts; attempt++) {
			int dx = level.getRandom().nextInt(3) - 1;
			int dz = level.getRandom().nextInt(3) - 1;
			if (dx == 0 && dz == 0) {
				continue;
			}
			if (!diagonals && Math.abs(dx) + Math.abs(dz) > 1) {
				continue;
			}

			BlockPos targetCrop = cropPos.offset(dx, 0, dz);
			BlockPos targetSoil = targetCrop.below();
			if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
				prepareBeetFarmland(level, targetSoil);
			}
			if (level.getBlockState(targetCrop).isAir() && canPlantOn(level.getBlockState(targetSoil))) {
				level.setBlock(targetCrop, Blocks.BEETROOTS.defaultBlockState(), 3);
			}
		}
	}

	private static void prepareBeetFarmland(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.is(ModBlocks.BEET_SOIL)) {
			level.setBlock(pos, ModBlocks.BEET_FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7), 3);
		} else if (state.is(ModBlocks.FERTILIZED_BEET_SOIL)) {
			level.setBlock(pos, ModBlocks.FERTILIZED_BEET_FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7), 3);
		}
	}

	private static boolean canPlantOn(BlockState state) {
		return state.is(Blocks.FARMLAND)
				|| state.is(ModBlocks.BEET_FARMLAND)
				|| state.is(ModBlocks.FERTILIZED_BEET_FARMLAND);
	}
}
