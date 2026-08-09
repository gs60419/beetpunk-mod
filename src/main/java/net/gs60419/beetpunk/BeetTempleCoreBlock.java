package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BeetTempleCoreBlock extends Block implements EntityBlock {
	public static final IntegerProperty GLOW = IntegerProperty.create("glow", 0, 13);
	public static final int MAX_TEMPLE_LEVEL = 4;

	private final BeetTempleTier tier;

	public BeetTempleCoreBlock(BeetTempleTier tier, BlockBehaviour.Properties properties) {
		super(properties);
		this.tier = tier;
		registerDefaultState(stateDefinition.any().setValue(GLOW, 0));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BeetTempleCoreBlockEntity(pos, state);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public boolean isActive(BlockState state) {
		return state.getValue(GLOW) > 0;
	}

	public BeetTempleTier tier() {
		return tier;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
											  BlockPos pos, Player player, InteractionHand hand,
											  BlockHitResult hit) {
		if (stack.is(ModItems.BEET_PILGRIM_BOOK)) {
			return handlePilgrimBook(state, level, pos, player);
		}

		if (stack.is(ModItems.BEET_REVELATION)) {
			return handleRevelation(stack, state, level, pos, player);
		}

		int scriptureLevel = scriptureLevel(stack);
		if (scriptureLevel == 0) {
			return player.isShiftKeyDown() ? InteractionResult.PASS : openCoreMenu(level, pos, player);
		}

		if (!level.isClientSide()) {
			insertScripture(stack, state, level, pos, player, scriptureLevel);
		}

		return InteractionResult.SUCCESS;
	}

	private InteractionResult handleRevelation(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
		if (!canUseRevelation(level, pos)) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
			BlockPos spawnPos = pos.above();
			if (tier == BeetTempleTier.DYE || tier == BeetTempleTier.PIG) {
				vanillaEntityType("pig").spawn(serverLevel, spawnPos, EntitySpawnReason.MOB_SUMMONED);
			} else {
				vanillaEntityType("villager").spawn(serverLevel, spawnPos, EntitySpawnReason.MOB_SUMMONED);
			}
			ModWorldPerformances.revelation(serverLevel, pos);
			level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.0F);
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}
		return InteractionResult.SUCCESS;
	}

	private static EntityType<?> vanillaEntityType(String path) {
		return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace(path));
	}

	private boolean canUseRevelation(Level level, BlockPos pos) {
		int levelValue = templeLevel(level, pos);
		return (tier == BeetTempleTier.DYE && levelValue >= MAX_TEMPLE_LEVEL)
				|| (tier == BeetTempleTier.PIG && levelValue >= 1)
				|| (tier == BeetTempleTier.SUPREME && levelValue >= 1);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!player.isShiftKeyDown()) {
			return openCoreMenu(level, pos, player);
		}

		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BeetTempleCoreBlockEntity core) {
			ItemStack removed = core.removeLast();
			if (!removed.isEmpty()) {
				player.getInventory().placeItemBackInInventory(removed);
				syncGlow(state, level, pos, core.level());
				level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.7F, 1.0F);
			}
		}
		return InteractionResult.SUCCESS;
	}

	private InteractionResult openCoreMenu(Level level, BlockPos pos, Player player) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(level.getBlockEntity(pos) instanceof BeetTempleCoreBlockEntity)) {
			BlockEntity blockEntity = newBlockEntity(pos, level.getBlockState(pos));
			if (blockEntity != null) {
				level.setBlockEntity(blockEntity);
			}
		}
		if (level.getBlockEntity(pos) instanceof MenuProvider provider) {
			player.openMenu(provider);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		if (level.getBlockEntity(pos) instanceof BeetTempleCoreBlockEntity core) {
			for (ItemStack stack : core.removeAll()) {
				Block.popResource(level, pos, stack);
			}
		}

		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
	}

	public static int templeLevel(Level level, BlockPos corePos) {
		if (level.getBlockEntity(corePos) instanceof BeetTempleCoreBlockEntity core) {
			return core.level();
		}

		BlockState coreState = level.getBlockState(corePos);
		if (!(coreState.getBlock() instanceof BeetTempleCoreBlock)) {
			return 0;
		}
		return Math.min(coreState.getValue(GLOW), MAX_TEMPLE_LEVEL);
	}

	private void insertScripture(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, int scriptureLevel) {
		if (!(level.getBlockEntity(pos) instanceof BeetTempleCoreBlockEntity core)) {
			return;
		}

		int currentLevel = core.level();
		if (scriptureLevel != currentLevel + 1 || currentLevel >= MAX_TEMPLE_LEVEL || !core.insert(stack, scriptureLevel)) {
			return;
		}

		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}

		syncGlow(state, level, pos, core.level());
		level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.8F, 0.9F + scriptureLevel * 0.15F);
		if (level instanceof ServerLevel serverLevel) {
			ModWorldPerformances.templeLevelUp(serverLevel, pos, core.level());
			ModTempleMilestones.onTempleLevelChanged(serverLevel, pos, tier, currentLevel, core.level());
		}

		if (player instanceof ServerPlayer serverPlayer) {
			boolean alreadySealed = ModAdvancements.isDone(serverPlayer, tier.sealTempleAdvancementId());
			ModAdvancements.award(serverPlayer, tier.sealTempleAdvancementId(), "sealed");
			int sealCount = templeSealCount(serverPlayer);
			if (!alreadySealed && sealCount >= BeetTempleTier.values().length && level instanceof ServerLevel serverLevel) {
				ModWorldPerformances.templeSeal(serverLevel, pos, sealCount, true);
			}
			if (tier == BeetTempleTier.PIG && scriptureLevel == MAX_TEMPLE_LEVEL && hasAllTwelveTempleSeals(serverPlayer)) {
				Block.popResource(level, pos, new ItemStack(ModItems.SUPREME_GLYPH));
			}
		}
	}

	public static void syncGlow(BlockState state, Level level, BlockPos pos, int coreLevel) {
		int glow = Math.max(0, Math.min(coreLevel, MAX_TEMPLE_LEVEL));
		if (state.getValue(GLOW) != glow) {
			level.setBlock(pos, state.setValue(GLOW, glow), 3);
		}
	}

	public int scriptureLevel(ItemStack stack) {
		for (int level = 1; level <= MAX_TEMPLE_LEVEL; level++) {
			if (stack.is(ModItems.scriptureFor(tier, level))) {
				return level;
			}
		}
		return 0;
	}

	private static boolean hasAllTwelveTempleSeals(ServerPlayer player) {
		for (BeetTempleTier templeTier : BeetTempleTier.values()) {
			if (templeTier == BeetTempleTier.SUPREME) {
				continue;
			}
			if (!ModAdvancements.isDone(player, templeTier.sealTempleAdvancementId())) {
				return false;
			}
		}
		return true;
	}

	private static int templeSealCount(ServerPlayer player) {
		int count = 0;
		for (BeetTempleTier templeTier : BeetTempleTier.values()) {
			if (ModAdvancements.isDone(player, templeTier.sealTempleAdvancementId())) {
				count++;
			}
		}
		return count;
	}

	private InteractionResult handlePilgrimBook(BlockState state, Level level,
													BlockPos pos, Player player) {
		if (!isActive(state)) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide()) {
			int sealCount = 0;
			if (player instanceof ServerPlayer serverPlayer) {
				sealCount = templeSealCount(serverPlayer);
			}
			if (level instanceof ServerLevel serverLevel) {
				ModWorldPerformances.templeSeal(serverLevel, pos, sealCount, false);
			}
			level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8F, 1.0F);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(GLOW);
	}
}
