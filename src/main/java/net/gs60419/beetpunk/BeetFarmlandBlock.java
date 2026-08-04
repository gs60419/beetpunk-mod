package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BeetFarmlandBlock extends FarmlandBlock {
	private static final float LV4_AUTO_GROW_CHANCE = 0.20F;

	public BeetFarmlandBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state.is(Blocks.DIRT) ? ModBlocks.BEET_SOIL.defaultBlockState() : state;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (keepMoistBySoilTemple(level, pos, state)) {
			return;
		}
		super.tick(state, level, pos, random);
		replaceVanillaDirt(level, pos);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (keepMoistBySoilTemple(level, pos, state)) {
			autoGrowByGrowthTemple(level, pos, random);
			return;
		}
		super.randomTick(state, level, pos, random);
		replaceVanillaDirt(level, pos);
		autoGrowByGrowthTemple(level, pos, random);
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
		entity.causeFallDamage(fallDistance, 1.0F, level.damageSources().fall());
	}

	private static void replaceVanillaDirt(Level level, BlockPos pos) {
		if (level.getBlockState(pos).is(Blocks.DIRT)) {
			level.setBlockAndUpdate(pos, ModBlocks.BEET_SOIL.defaultBlockState());
		}
	}

	private static boolean keepMoistBySoilTemple(ServerLevel level, BlockPos pos, BlockState state) {
		if (!level.getBlockState(pos).is(ModBlocks.BEET_FARMLAND)) {
			return false;
		}
		if (TempleEffects.nearbyTempleLevel(level, pos, ModBlocks.SOIL_TEMPLE_CORE) < BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
			return false;
		}
		if (state.getValue(MOISTURE) < 7) {
			level.setBlock(pos, state.setValue(MOISTURE, 7), 3);
		}
		return true;
	}

	protected static void autoGrowByGrowthTemple(ServerLevel level, BlockPos farmlandPos, RandomSource random) {
		if (random.nextFloat() >= LV4_AUTO_GROW_CHANCE) {
			return;
		}
		if (TempleEffects.nearbyTempleLevel(level, farmlandPos, ModBlocks.GROWTH_TEMPLE_CORE) < BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
			return;
		}
		BeetFertilizerItem.growBeetroot(level, farmlandPos.above(), 1);
	}
}
