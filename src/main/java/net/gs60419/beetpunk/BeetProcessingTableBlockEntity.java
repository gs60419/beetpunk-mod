package net.gs60419.beetpunk;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BeetProcessingTableBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
	private static final int BEET_BLOCK_TIME = 80;
	private static final int DRIED_BEET_BLOCK_TIME = 120;
	private static final int BEET_SEED_TIME = 40;
	private static final int STONE_ROUTE_TIME = 60;
	private static final int SOIL_ROUTE_TIME = 100;
	private static final int INPUT_SLOT = 0;
	private static final int PRIMARY_OUTPUT_SLOT = 1;
	private static final int SECONDARY_OUTPUT_SLOT = 2;
	private static final int SLOT_COUNT = 3;
	private static final int[] INPUT_SLOTS = {INPUT_SLOT};
	private static final int[] OUTPUT_SLOTS = {PRIMARY_OUTPUT_SLOT, SECONDARY_OUTPUT_SLOT};
	private static final int CRANK_TICKS = 16;
	private static final float MAX_SPIN_SPEED = 24.0F;
	private static final float SPIN_ACCELERATION = 4.0F;
	private static final float SPIN_DECAY = 0.82F;

	private ItemStack input = ItemStack.EMPTY;
	private ItemStack primaryOutput = ItemStack.EMPTY;
	private ItemStack secondaryOutput = ItemStack.EMPTY;
	private int progress;
	private int maxProgress;
	private int crankTicks;
	private float spinAngle;
	private float spinSpeed;
	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> progress;
				case 1 -> maxProgress;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> progress = value;
				case 1 -> maxProgress = value;
				default -> {
				}
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	};

	public BeetProcessingTableBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BEET_PROCESSING_TABLE, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, BeetProcessingTableBlockEntity blockEntity) {
		blockEntity.tickSpin();
		if (!level.isClientSide()
				&& BeetProcessingTableBlock.isPrayerBarrel(state)
				&& !hasCrankBaseBelow(level, pos)) {
			BeetProcessingTableBlock.setSpinning(level, pos, state, blockEntity.isCranking());
		}

		if (level.isClientSide() || blockEntity.crankTicks <= 0) {
			return;
		}

		ProcessingRecipe recipe = blockEntity.getCurrentRecipe((ServerLevel) level, pos);
		if (recipe == null || !blockEntity.canAcceptOutputs(recipe)) {
			if (blockEntity.progress != 0 || blockEntity.maxProgress != 0) {
				blockEntity.progress = 0;
				blockEntity.maxProgress = 0;
				blockEntity.setChanged();
			}
			return;
		}

		blockEntity.maxProgress = recipe.time();
		blockEntity.progress++;

		if (blockEntity.progress >= blockEntity.maxProgress) {
			blockEntity.input.shrink(1);
			if (blockEntity.input.isEmpty()) {
				blockEntity.input = ItemStack.EMPTY;
			}
			blockEntity.primaryOutput = merge(blockEntity.primaryOutput, recipe.primaryOutput());
			blockEntity.secondaryOutput = merge(blockEntity.secondaryOutput, recipe.secondaryOutput());
			blockEntity.progress = 0;
			blockEntity.maxProgress = 0;

			// 甜菜聖文字：萃取台輸出甜菜石塊時，附近玩家有機率掉落
			if (recipe.primaryOutput().is(ModBlocks.DRIED_BEET_BLOCK.asItem()) && level instanceof ServerLevel serverLevel) {
				Player nearest = serverLevel.getNearestPlayer(
						pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8.0, false);
				if (nearest instanceof ServerPlayer serverPlayer
						&& ModAdvancements.isDone(serverPlayer, BeetTempleTier.ROOT.sealTempleAdvancementId())
						&& level.getRandom().nextFloat() < 0.15f) {
					net.minecraft.world.level.block.Block.popResource(level, pos, new net.minecraft.world.item.ItemStack(ModItems.BEET_GLYPH));
				}
			}
		}

		blockEntity.setChanged();
	}

	public void crank() {
		crankTicks = CRANK_TICKS;
		spinSpeed = Math.min(MAX_SPIN_SPEED, spinSpeed + SPIN_ACCELERATION);
		setChanged();
	}

	public float spinAngle(float partialTick) {
		return spinAngle + spinSpeed * partialTick;
	}

	public boolean isCranking() {
		return crankTicks > 0 || spinSpeed > 0.1F;
	}

	public boolean insertInput(ItemStack stack) {
		if (!isProcessable(stack)) {
			return false;
		}

		if (input.isEmpty()) {
			input = stack.split(1);
			progress = 0;
			maxProgress = 0;
			setChanged();
			return true;
		}

		if (ItemStack.isSameItemSameComponents(input, stack) && input.getCount() < input.getMaxStackSize()) {
			stack.shrink(1);
			input.grow(1);
			setChanged();
			return true;
		}

		return false;
	}

	public ItemStack takeOutput() {
		if (!primaryOutput.isEmpty()) {
			ItemStack result = primaryOutput;
			primaryOutput = ItemStack.EMPTY;
			setChanged();
			return result;
		}

		if (!secondaryOutput.isEmpty()) {
			ItemStack result = secondaryOutput;
			secondaryOutput = ItemStack.EMPTY;
			setChanged();
			return result;
		}

		return ItemStack.EMPTY;
	}

	public boolean hasOutput() {
		return !primaryOutput.isEmpty() || !secondaryOutput.isEmpty();
	}

	public List<ItemStack> removeStoredStacks() {
		List<ItemStack> stacks = new ArrayList<>();
		addIfPresent(stacks, input);
		addIfPresent(stacks, primaryOutput);
		addIfPresent(stacks, secondaryOutput);

		input = ItemStack.EMPTY;
		primaryOutput = ItemStack.EMPTY;
		secondaryOutput = ItemStack.EMPTY;
		progress = 0;
		maxProgress = 0;
		crankTicks = 0;
		spinSpeed = 0.0F;
		setChanged();
		return stacks;
	}

	private ProcessingRecipe getCurrentRecipe(ServerLevel level, BlockPos pos) {
		return switch (stationType()) {
			case EXTRACTOR -> extractorRecipe(TempleEffects.nearbyTempleLevel(level, pos, ModBlocks.ROOT_TEMPLE_CORE));
			case GRINDER -> grinderRecipe();
			case WASHING -> washingRecipe();
			case NONE -> null;
		};
	}

	private ProcessingRecipe extractorRecipe(int rootLevel) {
		if (input.is(ModBlocks.BEET_BLOCK.asItem())) {
			return new ProcessingRecipe(
					new ItemStack(ModBlocks.DRIED_BEET_BLOCK),
					new ItemStack(ModItems.BEET_WATER_DROP, rootLevel >= 2 ? 2 : 1),
					rootTime(BEET_BLOCK_TIME, rootLevel));
		}

		if (input.is(ModBlocks.DRIED_BEET_BLOCK.asItem())) {
			return new ProcessingRecipe(
					new ItemStack(ModItems.BEET_RESIDUE, rootLevel >= 3 ? 2 : 1),
					new ItemStack(ModItems.BEET_OIL, rootLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 2 : 1),
					rootTime(DRIED_BEET_BLOCK_TIME, rootLevel));
		}

		if (input.is(Items.BEETROOT_SEEDS)) {
			return new ProcessingRecipe(new ItemStack(ModItems.BEET_RESIDUE), new ItemStack(ModItems.BEET_OIL), BEET_SEED_TIME);
		}

		return null;
	}

	private static int rootTime(int baseTime, int rootLevel) {
		return switch (rootLevel) {
			case 1 -> Math.max(10, baseTime * 4 / 5);
			case 2 -> Math.max(10, baseTime * 2 / 3);
			case 3 -> Math.max(10, baseTime / 2);
			default -> rootLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? Math.max(10, baseTime * 2 / 5) : baseTime;
		};
	}

	private ProcessingRecipe grinderRecipe() {
		if (input.is(ModBlocks.BEET_BRICK_BLOCK.asItem())) {
			return new ProcessingRecipe(new ItemStack(ModBlocks.BEET_COBBLESTONE), ItemStack.EMPTY, STONE_ROUTE_TIME);
		}

		if (input.is(ModBlocks.BEET_COBBLESTONE.asItem())) {
			return new ProcessingRecipe(new ItemStack(ModBlocks.BEET_GRAVEL), ItemStack.EMPTY, STONE_ROUTE_TIME);
		}

		if (input.is(ModBlocks.BEET_GRAVEL.asItem())) {
			return new ProcessingRecipe(new ItemStack(ModBlocks.BEET_SAND), new ItemStack(ModItems.BEET_CRYSTAL_GRAIN), STONE_ROUTE_TIME);
		}

		return null;
	}

	private ProcessingRecipe washingRecipe() {
		if (input.is(ModBlocks.BEET_SAND.asItem())) {
			return new ProcessingRecipe(new ItemStack(ModItems.BEET_IRON_DUST), new ItemStack(Items.CLAY_BALL), SOIL_ROUTE_TIME);
		}

		if (input.is(ModItems.BEET_CRYSTAL_GRAIN)) {
			return new ProcessingRecipe(new ItemStack(ModItems.BEET_REDSTONE_DUST), ItemStack.EMPTY, SOIL_ROUTE_TIME);
		}

		return null;
	}

	private StationType stationType() {
		if (getBlockState().is(ModBlocks.BEET_EXTRACTOR_BARREL)) {
			return StationType.EXTRACTOR;
		}

		if (getBlockState().is(ModBlocks.BEET_GRINDER_BARREL)) {
			return StationType.GRINDER;
		}

		if (getBlockState().is(ModBlocks.BEET_WASHING_BARREL)) {
			return StationType.WASHING;
		}

		return StationType.NONE;
	}

	private boolean canAcceptOutputs(ProcessingRecipe recipe) {
		return canMerge(primaryOutput, recipe.primaryOutput()) && canMerge(secondaryOutput, recipe.secondaryOutput());
	}

	public static boolean isProcessable(ItemStack stack) {
		return stack.is(ModBlocks.BEET_BLOCK.asItem())
			|| stack.is(ModBlocks.DRIED_BEET_BLOCK.asItem())
			|| stack.is(ModBlocks.BEET_BRICK_BLOCK.asItem())
			|| stack.is(ModBlocks.BEET_COBBLESTONE.asItem())
			|| stack.is(ModBlocks.BEET_GRAVEL.asItem())
			|| stack.is(ModBlocks.BEET_SAND.asItem())
			|| stack.is(ModItems.BEET_CRYSTAL_GRAIN)
			|| stack.is(Items.BEETROOT_SEEDS);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(switch (stationType()) {
			case EXTRACTOR -> "container.beetpunk.beet_extractor_barrel";
			case GRINDER -> "container.beetpunk.beet_grinder_barrel";
			case WASHING -> "container.beetpunk.beet_washing_barrel";
			case NONE -> "container.beetpunk.beet_crank_base";
		});
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new BeetProcessingTableMenu(containerId, playerInventory, this, data);
	}

	@Override
	public int getContainerSize() {
		return SLOT_COUNT;
	}

	@Override
	public boolean isEmpty() {
		return input.isEmpty() && primaryOutput.isEmpty() && secondaryOutput.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return switch (slot) {
			case INPUT_SLOT -> input;
			case PRIMARY_OUTPUT_SLOT -> primaryOutput;
			case SECONDARY_OUTPUT_SLOT -> secondaryOutput;
			default -> ItemStack.EMPTY;
		};
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack result = stack.split(amount);
		if (stack.isEmpty()) {
			setItem(slot, ItemStack.EMPTY);
		} else {
			setChanged();
		}
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		setItem(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		ItemStack limitedStack = stack.copy();
		if (limitedStack.getCount() > getMaxStackSize()) {
			limitedStack.setCount(getMaxStackSize());
		}

		switch (slot) {
			case INPUT_SLOT -> {
				if (!ItemStack.isSameItemSameComponents(input, limitedStack)) {
					progress = 0;
					maxProgress = 0;
				}
				input = limitedStack;
			}
			case PRIMARY_OUTPUT_SLOT -> primaryOutput = limitedStack;
			case SECONDARY_OUTPUT_SLOT -> secondaryOutput = limitedStack;
			default -> {
			}
		}
		setChanged();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == INPUT_SLOT && isProcessable(stack);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.UP) {
			return INPUT_SLOTS;
		}

		return OUTPUT_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return direction == Direction.UP && canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return direction != Direction.UP && (slot == PRIMARY_OUTPUT_SLOT || slot == SECONDARY_OUTPUT_SLOT);
	}

	@Override
	public boolean stillValid(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) {
			return false;
		}

		return player.distanceToSqr(
			worldPosition.getX() + 0.5D,
			worldPosition.getY() + 0.5D,
			worldPosition.getZ() + 0.5D
		) <= 64.0D;
	}

	@Override
	public void clearContent() {
		input = ItemStack.EMPTY;
		primaryOutput = ItemStack.EMPTY;
		secondaryOutput = ItemStack.EMPTY;
		progress = 0;
		maxProgress = 0;
		crankTicks = 0;
		spinSpeed = 0.0F;
		setChanged();
	}

	private void tickSpin() {
		if (crankTicks > 0) {
			crankTicks--;
			spinSpeed = Math.min(MAX_SPIN_SPEED, spinSpeed + 0.25F);
		} else {
			spinSpeed *= SPIN_DECAY;
			if (spinSpeed < 0.05F) {
				spinSpeed = 0.0F;
			}
		}

		if (spinSpeed > 0.0F) {
			spinAngle = (spinAngle + spinSpeed) % 360.0F;
		}
	}

	private static boolean canMerge(ItemStack target, ItemStack addition) {
		if (addition.isEmpty()) {
			return true;
		}

		return target.isEmpty()
			|| (ItemStack.isSameItemSameComponents(target, addition) && target.getCount() + addition.getCount() <= target.getMaxStackSize());
	}

	private static ItemStack merge(ItemStack target, ItemStack addition) {
		if (addition.isEmpty()) {
			return target;
		}

		if (target.isEmpty()) {
			return addition.copy();
		}

		target.grow(addition.getCount());
		return target;
	}

	private static void addIfPresent(List<ItemStack> stacks, ItemStack stack) {
		if (!stack.isEmpty()) {
			stacks.add(stack);
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (!input.isEmpty()) {
			output.store("Input", ItemStack.OPTIONAL_CODEC, input);
		}
		if (!primaryOutput.isEmpty()) {
			output.store("PrimaryOutput", ItemStack.OPTIONAL_CODEC, primaryOutput);
		}
		if (!secondaryOutput.isEmpty()) {
			output.store("SecondaryOutput", ItemStack.OPTIONAL_CODEC, secondaryOutput);
		}
		output.putInt("Progress", progress);
		output.putInt("MaxProgress", maxProgress);
	}

	@Override
	protected void loadAdditional(ValueInput inputData) {
		super.loadAdditional(inputData);
		input = loadStack(inputData, "Input");
		primaryOutput = loadStack(inputData, "PrimaryOutput");
		secondaryOutput = loadStack(inputData, "SecondaryOutput");
		progress = inputData.getIntOr("Progress", 0);
		maxProgress = inputData.getIntOr("MaxProgress", 0);
	}

	private static ItemStack loadStack(ValueInput inputData, String key) {
		return inputData.read(key, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
	}

	private static boolean hasCrankBaseBelow(Level level, BlockPos pos) {
		BlockPos scanPos = pos.below();
		for (int i = 0; i < 4; i++) {
			if (level.getBlockEntity(scanPos) instanceof BeetCrankBaseBlockEntity) {
				return true;
			}
			if (!BeetProcessingTableBlock.isPrayerBarrel(level.getBlockState(scanPos))) {
				return false;
			}
			scanPos = scanPos.below();
		}
		return false;
	}

	private record ProcessingRecipe(ItemStack primaryOutput, ItemStack secondaryOutput, int time) {
	}

	private enum StationType {
		EXTRACTOR,
		GRINDER,
		WASHING,
		NONE
	}
}
