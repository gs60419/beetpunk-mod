package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BeetFertilizerItem extends Item {
	private static final float LV4_FERTILIZER_SAVE_CHANCE = 0.50F;

	public BeetFertilizerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		if (state.is(Blocks.BEETROOTS)) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}

			ServerLevel serverLevel = (ServerLevel) level;
			int templeLevel = TempleEffects.nearbyTempleLevel(serverLevel, pos, ModBlocks.GROWTH_TEMPLE_CORE);
			if (growArea(serverLevel, pos, templeLevel) == 0) {
				return InteractionResult.FAIL;
			}

			level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 0.9F, 1.15F);
			if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
				if (templeLevel < BeetTempleCoreBlock.MAX_TEMPLE_LEVEL || level.getRandom().nextFloat() >= LV4_FERTILIZER_SAVE_CHANCE) {
					context.getItemInHand().shrink(1);
				}
			}
			return InteractionResult.SUCCESS;
		}

		BlockState upgraded = upgradedState(state);
		if (upgraded == null) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide()) {
			level.setBlock(pos, upgraded, 3);
			level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.9F, 1.15F);
			if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
				context.getItemInHand().shrink(1);
			}
		}
		return InteractionResult.SUCCESS;
	}

	private static int growArea(ServerLevel level, BlockPos center, int templeLevel) {
		int radius = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 2 : templeLevel >= 3 ? 1 : 0;
		int steps = templeLevel >= 2 ? 2 : 1;
		int grown = 0;
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				BlockPos target = center.offset(x, 0, z);
				if (growBeetroot(level, target, steps)) {
					grown++;
				}
			}
		}
		return grown;
	}

	public static boolean growBeetroot(ServerLevel level, BlockPos pos, int steps) {
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

	private static BlockState upgradedState(BlockState state) {
		if (state.is(ModBlocks.BEET_SOIL)) {
			return ModBlocks.FERTILIZED_BEET_SOIL.defaultBlockState();
		}
		if (state.is(ModBlocks.BEET_FARMLAND)) {
			return ModBlocks.FERTILIZED_BEET_FARMLAND.defaultBlockState()
					.setValue(FarmlandBlock.MOISTURE, state.getValue(FarmlandBlock.MOISTURE));
		}
		return null;
	}
}
