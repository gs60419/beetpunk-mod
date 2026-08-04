package net.gs60419.beetpunk;

import java.util.HashSet;
import java.util.Set;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class ModGeothermalEffects {
	private static final int CHECK_INTERVAL_TICKS = 200;
	private static final int CORE_SEARCH_RADIUS = 64;
	private static final float LAVA_FILL_CHANCE = 0.025F;
	private static int ticks;

	private ModGeothermalEffects() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ticks++;
			if (ticks < CHECK_INTERVAL_TICKS) {
				return;
			}
			ticks = 0;

			for (ServerLevel level : server.getAllLevels()) {
				for (BlockPos corePos : findNearbyGeothermalCores(level)) {
					tryFillLavaCauldron(level, corePos);
				}
			}
		});
		Beetpunk.LOGGER.info("Registering Beetpunk geothermal effects.");
	}

	private static Set<BlockPos> findNearbyGeothermalCores(ServerLevel level) {
		Set<BlockPos> cores = new HashSet<>();
		level.players().forEach(player -> {
			BlockPos origin = player.blockPosition();
			for (BlockPos target : BlockPos.betweenClosed(
					origin.offset(-CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS),
					origin.offset(CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS))) {
				if (level.getBlockState(target).is(ModBlocks.BEET_GEOTHERMAL_CORE)) {
					cores.add(target.immutable());
				}
			}
		});
		return cores;
	}

	private static void tryFillLavaCauldron(ServerLevel level, BlockPos corePos) {
		if (TempleEffects.nearbyTempleLevel(level, corePos, ModBlocks.LIGHT_TEMPLE_CORE) < BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
			return;
		}
		if (level.getRandom().nextFloat() >= LAVA_FILL_CHANCE) {
			return;
		}

		BlockPos cauldronPos = corePos.above();
		BlockPos dripstonePos = corePos.above(3);
		if (!level.getBlockState(cauldronPos).is(Blocks.CAULDRON) || !level.getBlockState(corePos.above(2)).isAir()) {
			return;
		}
		if (!isDownwardPointedDripstone(level.getBlockState(dripstonePos))) {
			return;
		}

		level.setBlock(cauldronPos, Blocks.LAVA_CAULDRON.defaultBlockState(), 3);
		level.playSound(null, cauldronPos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 0.8F, 1.0F);
	}

	private static boolean isDownwardPointedDripstone(BlockState state) {
		return state.is(Blocks.POINTED_DRIPSTONE)
				&& state.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN;
	}
}
