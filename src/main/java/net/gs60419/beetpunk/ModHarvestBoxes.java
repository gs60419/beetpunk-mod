package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public final class ModHarvestBoxes {
	private static final int SEARCH_RADIUS = 8;

	private ModHarvestBoxes() {
	}

	public static boolean tryInsertNearby(ServerLevel level, BlockPos origin, ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}

		BlockPos bestPos = null;
		double bestDistance = Double.MAX_VALUE;
		for (BlockPos target : BlockPos.betweenClosed(
				origin.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
				origin.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
			if (!(level.getBlockEntity(target) instanceof BeetHarvestBoxBlockEntity harvestBox)) {
				continue;
			}
			double distance = target.distSqr(origin);
			if (distance >= bestDistance || !harvestBox.canAccept(stack)) {
				continue;
			}
			bestDistance = distance;
			bestPos = target.immutable();
		}
		return bestPos != null
				&& level.getBlockEntity(bestPos) instanceof BeetHarvestBoxBlockEntity harvestBox
				&& harvestBox.insert(stack.copy());
	}
}
