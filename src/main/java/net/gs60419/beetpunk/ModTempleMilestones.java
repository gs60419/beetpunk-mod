package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class ModTempleMilestones {
	private ModTempleMilestones() {
	}

	public static void onTempleLevelChanged(ServerLevel level, BlockPos pos, BeetTempleTier tier, int oldLevel, int newLevel) {
		if (oldLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL || newLevel < BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
			return;
		}

		TempleActivationData data = TempleActivationData.get(level);
		if (!data.markTempleLv4(tier)) {
			return;
		}

		int lv4Count = data.lv4TempleCount();
		triggerMilestone(level, pos, data, lv4Count, 1, "first_lv4");
		triggerMilestone(level, pos, data, lv4Count, 4, "four_lv4");
		triggerMilestone(level, pos, data, lv4Count, 12, "twelve_lv4");
		triggerMilestone(level, pos, data, lv4Count, 13, "thirteen_lv4");
	}

	private static void triggerMilestone(ServerLevel level, BlockPos pos, TempleActivationData data, int lv4Count, int threshold, String id) {
		if (lv4Count >= threshold && data.markMilestone(id)) {
			ModWorldPerformances.templeMilestone(level, pos, threshold);
		}
	}
}
