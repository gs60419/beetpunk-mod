package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FertilizedBeetFarmlandBlock extends BeetFarmlandBlock {
	public FertilizedBeetFarmlandBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		keepMoist(level, pos, state);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		keepMoist(level, pos, state);
		autoGrowByGrowthTemple(level, pos, random);
	}

	private static void keepMoist(ServerLevel level, BlockPos pos, BlockState state) {
		if (level.getBlockState(pos).is(ModBlocks.FERTILIZED_BEET_FARMLAND) && state.getValue(MOISTURE) < 7) {
			level.setBlock(pos, state.setValue(MOISTURE, 7), 3);
		}
	}
}
