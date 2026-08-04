package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeetProcessingTableBlock extends Block implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty SPINNING = BooleanProperty.create("spinning");
	private final boolean crankable;

	public BeetProcessingTableBlock(BlockBehaviour.Properties properties) {
		this(properties, true);
	}

	public BeetProcessingTableBlock(BlockBehaviour.Properties properties, boolean crankable) {
		super(properties);
		this.crankable = crankable;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SPINNING, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BeetProcessingTableBlockEntity(pos, state);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected boolean shouldChangedStateKeepBlockEntity(BlockState state) {
		return true;
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state) {
		if (isPrayerBarrel(state) && state.hasProperty(SPINNING) && state.getValue(SPINNING)) {
			return Shapes.empty();
		}
		return super.getOcclusionShape(state);
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return isPrayerBarrel(state) && state.hasProperty(SPINNING) && state.getValue(SPINNING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, SPINNING);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (type != ModBlockEntities.BEET_PROCESSING_TABLE) {
			return null;
		}

		return (tickerLevel, tickerPos, tickerState, blockEntity) ->
			BeetProcessingTableBlockEntity.tick(tickerLevel, tickerPos, tickerState, (BeetProcessingTableBlockEntity) blockEntity);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return interact(state, level, pos, player);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return interact(state, level, pos, player);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		if (level.getBlockEntity(pos) instanceof BeetProcessingTableBlockEntity processingTable) {
			for (ItemStack stack : processingTable.removeStoredStacks()) {
				Block.popResource(level, pos, stack);
			}
		}

		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
	}

	private static InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos.below()) instanceof BeetCrankBaseBlockEntity base) {
			if (player.isShiftKeyDown()) {
				if (level.isClientSide()) {
					return InteractionResult.SUCCESS;
				}
				player.openMenu(base);
				return InteractionResult.SUCCESS;
			}
			if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
				setSpinning(level, pos, state, true);
				base.manualCrank(serverLevel, pos.below());
				level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.45F, 0.85F + level.getRandom().nextFloat() * 0.25F);
			} else if (level.isClientSide()) {
				setSpinning(level, pos, state, true);
				if (level.getBlockEntity(pos) instanceof BeetProcessingTableBlockEntity processingTable) {
					processingTable.crank();
				}
				base.visualCrank();
				level.addParticle(ParticleTypes.CRIT,
					pos.getX() + 0.5D,
					pos.getY() + 0.5D,
					pos.getZ() + 0.5D,
					0.0D, 0.05D, 0.0D);
			}
			return InteractionResult.SUCCESS;
		}

		if (isPrayerBarrel(state)) {
			if (level.getBlockEntity(pos) instanceof BeetProcessingTableBlockEntity processingTable) {
				if (!level.isClientSide()) {
					setSpinning(level, pos, state, true);
				}
				processingTable.crank();
				if (level.isClientSide()) {
					level.addParticle(ParticleTypes.CRIT,
						pos.getX() + 0.5D,
						pos.getY() + 1.05D,
						pos.getZ() + 0.5D,
						0.0D, 0.04D, 0.0D);
				} else {
					level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.35F, 0.7F + level.getRandom().nextFloat() * 0.25F);
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}

		if (player.isShiftKeyDown()) {
			return openMenu(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}

		if (!processingTableShouldCrank(state, level, pos, player)) {
			return InteractionResult.PASS;
		}

		if (level.getBlockEntity(pos) instanceof BeetProcessingTableBlockEntity processingTable) {
			processingTable.crank();
			if (level.getBlockEntity(pos.below()) instanceof BeetCrankBaseBlockEntity base && base.isBurning()) {
				processingTable.crank();
			}
			if (level.isClientSide()) {
				level.addParticle(ParticleTypes.CRIT,
					pos.getX() + 0.5D,
					pos.getY() + 1.05D,
					pos.getZ() + 0.5D,
					0.0D, 0.04D, 0.0D);
			} else {
				level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.35F, 0.7F + level.getRandom().nextFloat() * 0.25F);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	static void setSpinning(Level level, BlockPos pos, BlockState state, boolean spinning) {
		if (state.hasProperty(SPINNING) && state.getValue(SPINNING) != spinning) {
			level.setBlock(pos, state.setValue(SPINNING, spinning), Block.UPDATE_ALL_IMMEDIATE);
		}
	}

	static boolean isPrayerBarrel(BlockState state) {
		return state.is(ModBlocks.BEET_EXTRACTOR_BARREL)
			|| state.is(ModBlocks.BEET_GRINDER_BARREL)
			|| state.is(ModBlocks.BEET_WASHING_BARREL);
	}

	private static boolean processingTableShouldCrank(BlockState state, Level level, BlockPos pos, Player player) {
		return state.getBlock() instanceof BeetProcessingTableBlock block && block.crankable;
	}

	static boolean openMenu(Level level, BlockPos pos, Player player) {
		if (level.isClientSide()) {
			return true;
		}

		if (level.getBlockEntity(pos) instanceof BeetProcessingTableBlockEntity processingTable) {
			player.openMenu(processingTable);
			return true;
		}

		return false;
	}
}
