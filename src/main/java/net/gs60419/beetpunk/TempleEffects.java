package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public final class TempleEffects {
	private static final int MAX_TEMPLE_RANGE = 64;

	private TempleEffects() {
	}

	public static int nearbyTempleLevel(ServerLevel level, BlockPos origin, Block templeCore) {
		int bestLevel = 0;
		for (BlockPos target : BlockPos.betweenClosed(
				origin.offset(-MAX_TEMPLE_RANGE, -MAX_TEMPLE_RANGE, -MAX_TEMPLE_RANGE),
				origin.offset(MAX_TEMPLE_RANGE, MAX_TEMPLE_RANGE, MAX_TEMPLE_RANGE))) {
			if (!level.getBlockState(target).is(templeCore)) {
				continue;
			}

			int templeLevel = BeetTempleCoreBlock.templeLevel(level, target);
			if (!isInTempleRange(origin, target, templeLevel)) {
				continue;
			}

			bestLevel = Math.max(bestLevel, templeLevel);
			if (bestLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
				return bestLevel;
			}
		}
		return bestLevel;
	}

	public static int templeRange(int templeLevel) {
		return Math.max(0, Math.min(templeLevel, BeetTempleCoreBlock.MAX_TEMPLE_LEVEL)) * 16;
	}

	private static boolean isInTempleRange(BlockPos origin, BlockPos templePos, int templeLevel) {
		int range = templeRange(templeLevel);
		if (range <= 0) {
			return false;
		}
		return Math.abs(origin.getX() - templePos.getX()) <= range
				&& Math.abs(origin.getY() - templePos.getY()) <= range
				&& Math.abs(origin.getZ() - templePos.getZ()) <= range;
	}
}
