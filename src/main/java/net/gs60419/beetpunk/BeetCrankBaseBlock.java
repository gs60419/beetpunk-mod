package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BeetCrankBaseBlock extends Block implements EntityBlock {
	public BeetCrankBaseBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BeetCrankBaseBlockEntity(pos, state);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (type != ModBlockEntities.BEET_CRANK_BASE) {
			return null;
		}

		return (tickerLevel, tickerPos, tickerState, blockEntity) ->
			BeetCrankBaseBlockEntity.tick(tickerLevel, tickerPos, tickerState, (BeetCrankBaseBlockEntity) blockEntity);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return interact(level, pos, player);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return interact(level, pos, player);
	}

	private static InteractionResult interact(Level level, BlockPos pos, Player player) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (level.getBlockEntity(pos) instanceof BeetCrankBaseBlockEntity base) {
			player.openMenu(base);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		if (level.getBlockEntity(pos) instanceof BeetCrankBaseBlockEntity base) {
			for (ItemStack stack : base.removeStoredStacks()) {
				Block.popResource(level, pos, stack);
			}
		}

		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
	}
}
