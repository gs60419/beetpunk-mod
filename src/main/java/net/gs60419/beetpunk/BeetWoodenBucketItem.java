package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BeetWoodenBucketItem extends Item {
	private final boolean filled;

	public BeetWoodenBucketItem(boolean filled, Properties properties) {
		super(properties);
		this.filled = filled;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult hit = getPlayerPOVHitResult(level, player, filled ? ClipContext.Fluid.NONE : ClipContext.Fluid.SOURCE_ONLY);
		if (hit.getType() == HitResult.Type.MISS) {
			return InteractionResult.PASS;
		}

		if (filled) {
			return placeWater(level, player, hand, stack, hit);
		}
		return pickUpWater(level, player, hand, stack, hit);
	}

	private InteractionResult pickUpWater(Level level, Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
		BlockPos pos = hit.getBlockPos();
		if (!level.mayInteract(player, pos)) {
			return InteractionResult.FAIL;
		}

		BlockState state = level.getBlockState(pos);
		if (!state.getFluidState().is(Fluids.WATER) || !state.getFluidState().isSource()) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide()) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
			level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
			replaceHeldItem(player, hand, stack, new ItemStack(ModItems.BEET_WOODEN_WATER_BUCKET));
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResult.SUCCESS;
	}

	private InteractionResult placeWater(Level level, Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
		BlockPos clicked = hit.getBlockPos();
		Direction direction = hit.getDirection();
		BlockPos target = level.getBlockState(clicked).canBeReplaced() ? clicked : clicked.relative(direction);
		if (!level.mayInteract(player, target) || !player.mayUseItemAt(target, direction, stack)) {
			return InteractionResult.FAIL;
		}

		BlockState targetState = level.getBlockState(target);
		if (!targetState.canBeReplaced()) {
			return InteractionResult.FAIL;
		}

		if (!level.isClientSide()) {
			level.setBlock(target, Blocks.WATER.defaultBlockState(), 11);
			level.playSound(null, target, SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
			if (level instanceof ServerLevel serverLevel) {
				Blocks.WATER.defaultBlockState().updateNeighbourShapes(serverLevel, target, 3);
			}
			replaceHeldItem(player, hand, stack, new ItemStack(ModItems.BEET_WOODEN_BUCKET));
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResult.SUCCESS;
	}

	private static void replaceHeldItem(Player player, InteractionHand hand, ItemStack stack, ItemStack replacement) {
		if (player.getAbilities().instabuild) {
			return;
		}
		stack.shrink(1);
		if (stack.isEmpty()) {
			player.setItemInHand(hand, replacement);
		} else if (!player.getInventory().add(replacement)) {
			player.drop(replacement, false);
		}
	}
}
