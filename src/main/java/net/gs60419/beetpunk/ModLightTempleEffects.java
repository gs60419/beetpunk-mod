package net.gs60419.beetpunk;

import java.util.HashSet;
import java.util.Set;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

public final class ModLightTempleEffects {
	private static final int CHECK_INTERVAL_TICKS = 100;
	private static final int CORE_SEARCH_RADIUS = 64;
	private static int ticks;

	private ModLightTempleEffects() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ticks++;
			if (ticks < CHECK_INTERVAL_TICKS) {
				return;
			}
			ticks = 0;

			for (ServerLevel level : server.getAllLevels()) {
				Set<BlockPos> cores = findNearbyLightCores(level);
				for (BlockPos corePos : cores) {
					applyLightField(level, corePos);
				}
			}
		});
		Beetpunk.LOGGER.info("Registering Beetpunk light temple effects.");
	}

	private static Set<BlockPos> findNearbyLightCores(ServerLevel level) {
		Set<BlockPos> cores = new HashSet<>();
		level.players().forEach(player -> {
			BlockPos origin = player.blockPosition();
			for (BlockPos target : BlockPos.betweenClosed(
					origin.offset(-CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS),
					origin.offset(CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS))) {
				if (level.getBlockState(target).is(ModBlocks.LIGHT_TEMPLE_CORE)
						&& BeetTempleCoreBlock.templeLevel(level, target) > 0) {
					cores.add(target.immutable());
				}
			}
		});
		return cores;
	}

	private static void applyLightField(ServerLevel level, BlockPos corePos) {
		int templeLevel = BeetTempleCoreBlock.templeLevel(level, corePos);
		int range = TempleEffects.templeRange(templeLevel);
		if (range <= 0) {
			return;
		}

		AABB area = new AABB(corePos).inflate(range);
		for (Monster monster : level.getEntitiesOfClass(Monster.class, area, monster -> monster.isAlive())) {
			if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL && !monster.hasCustomName()) {
				monster.discard();
				continue;
			}

			if (templeLevel >= 1) {
				monster.addEffect(new MobEffectInstance(MobEffects.GLOWING, CHECK_INTERVAL_TICKS + 20, 0, true, false));
			}
			if (templeLevel >= 2) {
				monster.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, CHECK_INTERVAL_TICKS + 20, 0, true, false));
			}
			if (templeLevel >= 3) {
				monster.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, CHECK_INTERVAL_TICKS + 20, 0, true, false));
			}
		}
	}
}
