package net.gs60419.beetpunk;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BeetCrankBaseBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
	private static final int BEET_BLOCK_TIME = 80;
	private static final int DRIED_BEET_BLOCK_TIME = 120;
	private static final int BEET_SEED_TIME = 40;
	private static final int STONE_ROUTE_TIME = 60;
	private static final int SOIL_ROUTE_TIME = 100;
	private static final int AUTO_CRANK_INTERVAL = 8;
	private static final int MAX_BARREL_STACK = 4;
	private static final int CRANK_TICKS = 16;
	private static final float MAX_SPIN_SPEED = 24.0F;
	private static final float SPIN_ACCELERATION = 4.0F;
	private static final float BURN_SPIN_ACCELERATION = 0.35F;
	private static final float SPIN_DECAY = 0.84F;
	private static final int INPUT_SLOT = 0;
	private static final int FUEL_SLOT = 1;
	private static final int PRIMARY_OUTPUT_SLOT = 2;
	private static final int SECONDARY_OUTPUT_SLOT = 3;
	private static final int SLOT_COUNT = 4;
	private static final int[] INPUT_SLOTS = {INPUT_SLOT};
	private static final int[] FUEL_SLOTS = {FUEL_SLOT};
	private static final int[] OUTPUT_SLOTS = {PRIMARY_OUTPUT_SLOT, SECONDARY_OUTPUT_SLOT};

	private ItemStack input = ItemStack.EMPTY;
	private ItemStack fuel = ItemStack.EMPTY;
	private ItemStack primaryOutput = ItemStack.EMPTY;
	private ItemStack secondaryOutput = ItemStack.EMPTY;
	private int progress;
	private int maxProgress;
	private int burnTicks;
	private int maxBurnTicks;
	private int autoCrankCooldown;
	private int crankTicks;
	private float spinAngle;
	private float spinSpeed;
	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> progress;
				case 1 -> maxProgress;
				case 2 -> burnTicks;
				case 3 -> maxBurnTicks;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0 -> progress = value;
				case 1 -> maxProgress = value;
				case 2 -> burnTicks = value;
				case 3 -> maxBurnTicks = value;
				default -> {
				}
			}
		}

		@Override
		public int getCount() {
			return 4;
		}
	};

	public BeetCrankBaseBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BEET_CRANK_BASE, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, BeetCrankBaseBlockEntity blockEntity) {
		if (level.isClientSide()) {
			blockEntity.tickSpin();
			return;
		}

		if (blockEntity.burnTicks <= 0) {
			blockEntity.tryConsumeFuel();
		}

		if (blockEntity.burnTicks > 0) {
			blockEntity.setAboveBarrelSpinning(true);
			blockEntity.burnTicks--;
			if (blockEntity.autoCrankCooldown > 0) {
				blockEntity.autoCrankCooldown--;
			}
			if (blockEntity.autoCrankCooldown <= 0) {
				blockEntity.autoCrankCooldown = AUTO_CRANK_INTERVAL;
				blockEntity.processSteps((ServerLevel) level, pos, blockEntity.barrelStackHeight(pos));
			}
			blockEntity.setChanged();
			return;
		}

		if (blockEntity.progress != 0 || blockEntity.maxProgress != 0 || blockEntity.maxBurnTicks != 0) {
			blockEntity.progress = 0;
			blockEntity.maxProgress = 0;
			blockEntity.maxBurnTicks = 0;
			blockEntity.setChanged();
		}
		if (blockEntity.crankTicks > 0) {
			blockEntity.crankTicks--;
			blockEntity.spinSpeed = Math.min(MAX_SPIN_SPEED, blockEntity.spinSpeed + 0.25F);
		} else {
			blockEntity.spinSpeed *= SPIN_DECAY;
			if (blockEntity.spinSpeed < 0.05F) {
				blockEntity.spinSpeed = 0.0F;
			}
		}
		blockEntity.setAboveBarrelSpinning(blockEntity.crankTicks > 0 || blockEntity.spinSpeed > 0.1F);
	}

	public void manualCrank(ServerLevel level, BlockPos pos) {
		visualCrank();
		setBarrelStackSpinning(true);
		int steps = barrelStackHeight(pos);
		processSteps(level, pos, steps);
		if (burnTicks > 0) {
			processSteps(level, pos, steps);
		}
		syncClient();
	}

	public boolean isBurning() {
		return burnTicks > 0;
	}

	public void visualCrank() {
		crankTicks = CRANK_TICKS;
		spinSpeed = Math.min(MAX_SPIN_SPEED, spinSpeed + SPIN_ACCELERATION);
		setChanged();
	}

	public float spinAngle(float partialTick) {
		return spinAngle + spinSpeed * partialTick;
	}

	public boolean isSpinning() {
		return isBurning() || crankTicks > 0 || spinSpeed > 0.1F;
	}

	public List<ItemStack> removeStoredStacks() {
		List<ItemStack> stacks = new ArrayList<>();
		addIfPresent(stacks, input);
		addIfPresent(stacks, fuel);
		addIfPresent(stacks, primaryOutput);
		addIfPresent(stacks, secondaryOutput);
		input = ItemStack.EMPTY;
		fuel = ItemStack.EMPTY;
		primaryOutput = ItemStack.EMPTY;
		secondaryOutput = ItemStack.EMPTY;
		progress = 0;
		maxProgress = 0;
		crankTicks = 0;
		spinSpeed = 0.0F;
		setChanged();
		return stacks;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.beetpunk.beet_crank_base");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new BeetCrankBaseMenu(containerId, playerInventory, this, data);
	}

	@Override
	public int getContainerSize() {
		return SLOT_COUNT;
	}

	@Override
	public boolean isEmpty() {
		return input.isEmpty() && fuel.isEmpty() && primaryOutput.isEmpty() && secondaryOutput.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return switch (slot) {
			case INPUT_SLOT -> input;
			case FUEL_SLOT -> fuel;
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
			case FUEL_SLOT -> fuel = limitedStack;
			case PRIMARY_OUTPUT_SLOT -> primaryOutput = limitedStack;
			case SECONDARY_OUTPUT_SLOT -> secondaryOutput = limitedStack;
			default -> {
			}
		}
		setChanged();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return switch (slot) {
			case INPUT_SLOT -> BeetProcessingTableBlockEntity.isProcessable(stack);
			case FUEL_SLOT -> ModFuels.fuelTicks(stack) > 0;
			default -> false;
		};
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.UP) {
			return INPUT_SLOTS;
		}
		if (side == Direction.DOWN) {
			return FUEL_SLOTS;
		}
		return OUTPUT_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return (slot == INPUT_SLOT && direction == Direction.UP && canPlaceItem(slot, stack))
			|| (slot == FUEL_SLOT && direction != Direction.UP && canPlaceItem(slot, stack));
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return slot == PRIMARY_OUTPUT_SLOT || slot == SECONDARY_OUTPUT_SLOT;
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
		fuel = ItemStack.EMPTY;
		primaryOutput = ItemStack.EMPTY;
		secondaryOutput = ItemStack.EMPTY;
		progress = 0;
		maxProgress = 0;
		crankTicks = 0;
		spinSpeed = 0.0F;
		setChanged();
	}

	private void processSteps(ServerLevel level, BlockPos pos, int steps) {
		for (int i = 0; i < Math.max(1, steps); i++) {
			if (!processStep(level, pos)) {
				return;
			}
		}
	}

	private boolean processStep(ServerLevel level, BlockPos pos) {
		ProcessingRecipe recipe = getCurrentRecipe(level, pos);
		if (recipe == null || !canAcceptOutputs(recipe)) {
			if (progress != 0 || maxProgress != 0) {
				progress = 0;
				maxProgress = 0;
				setChanged();
			}
			return false;
		}

		maxProgress = recipe.time();
		progress++;
		if (progress >= maxProgress) {
			input.shrink(1);
			if (input.isEmpty()) {
				input = ItemStack.EMPTY;
			}
			primaryOutput = merge(primaryOutput, recipe.primaryOutput());
			secondaryOutput = merge(secondaryOutput, recipe.secondaryOutput());
			progress = 0;
			maxProgress = 0;

			if (recipe.primaryOutput().is(ModBlocks.DRIED_BEET_BLOCK.asItem())) {
				Player nearest = level.getNearestPlayer(
						pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8.0, false);
				if (nearest instanceof ServerPlayer serverPlayer
						&& ModAdvancements.isDone(serverPlayer, BeetTempleTier.ROOT.sealTempleAdvancementId())
						&& level.getRandom().nextFloat() < 0.15f) {
					net.minecraft.world.level.block.Block.popResource(level, pos, new ItemStack(ModItems.BEET_GLYPH));
				}
			}
		}
		setChanged();
		return true;
	}

	private void tryConsumeFuel() {
		int ticks = ModFuels.fuelTicks(fuel);
		if (ticks <= 0) {
			return;
		}
		fuel.shrink(1);
		if (fuel.isEmpty()) {
			fuel = ItemStack.EMPTY;
		}
		burnTicks = ticks;
		maxBurnTicks = ticks;
		autoCrankCooldown = 1;
		setChanged();
		syncClient();
	}

	private void tickSpin() {
		if (burnTicks > 0) {
			burnTicks--;
			spinSpeed = Math.min(MAX_SPIN_SPEED, spinSpeed + BURN_SPIN_ACCELERATION);
		} else if (crankTicks > 0) {
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

	private void syncClient() {
		if (level != null && !level.isClientSide()) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
		}
	}

	private void setAboveBarrelSpinning(boolean spinning) {
		setBarrelStackSpinning(spinning);
	}

	private void setBarrelStackSpinning(boolean spinning) {
		if (level == null || level.isClientSide()) {
			return;
		}
		StationType type = stationType(worldPosition);
		if (type == StationType.NONE) {
			return;
		}
		for (int i = 1; i <= MAX_BARREL_STACK; i++) {
			BlockPos barrelPos = worldPosition.above(i);
			BlockState barrelState = level.getBlockState(barrelPos);
			if (stationTypeForState(barrelState) != type) {
				return;
			}
			BeetProcessingTableBlock.setSpinning(level, barrelPos, barrelState, spinning);
		}
	}

	private static boolean isPrayerBarrel(BlockState state) {
		return BeetProcessingTableBlock.isPrayerBarrel(state);
	}

	private ProcessingRecipe getCurrentRecipe(ServerLevel level, BlockPos pos) {
		return switch (stationType(pos)) {
			case EXTRACTOR -> extractorRecipe(TempleEffects.nearbyTempleLevel(level, pos, ModBlocks.ROOT_TEMPLE_CORE));
			case GRINDER -> grinderRecipe();
			case WASHING -> washingRecipe();
			case NONE -> null;
		};
	}

	private StationType stationType(BlockPos pos) {
		if (level == null) {
			return StationType.NONE;
		}
		return stationTypeForState(level.getBlockState(pos.above()));
	}

	private int barrelStackHeight(BlockPos pos) {
		if (level == null) {
			return 1;
		}
		StationType type = stationType(pos);
		if (type == StationType.NONE) {
			return 1;
		}
		int height = 0;
		for (int i = 1; i <= MAX_BARREL_STACK; i++) {
			if (stationTypeForState(level.getBlockState(pos.above(i))) != type) {
				break;
			}
			height++;
		}
		return Math.max(1, height);
	}

	static StationType stationTypeForState(BlockState barrelState) {
		if (barrelState.is(ModBlocks.BEET_EXTRACTOR_BARREL)) {
			return StationType.EXTRACTOR;
		}
		if (barrelState.is(ModBlocks.BEET_GRINDER_BARREL)) {
			return StationType.GRINDER;
		}
		if (barrelState.is(ModBlocks.BEET_WASHING_BARREL)) {
			return StationType.WASHING;
		}
		return StationType.NONE;
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

	private boolean canAcceptOutputs(ProcessingRecipe recipe) {
		return canMerge(primaryOutput, recipe.primaryOutput()) && canMerge(secondaryOutput, recipe.secondaryOutput());
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
		if (!fuel.isEmpty()) {
			output.store("Fuel", ItemStack.OPTIONAL_CODEC, fuel);
		}
		if (!primaryOutput.isEmpty()) {
			output.store("PrimaryOutput", ItemStack.OPTIONAL_CODEC, primaryOutput);
		}
		if (!secondaryOutput.isEmpty()) {
			output.store("SecondaryOutput", ItemStack.OPTIONAL_CODEC, secondaryOutput);
		}
		output.putInt("Progress", progress);
		output.putInt("MaxProgress", maxProgress);
		output.putInt("BurnTicks", burnTicks);
		output.putInt("MaxBurnTicks", maxBurnTicks);
		output.putInt("AutoCrankCooldown", autoCrankCooldown);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		return saveWithoutMetadata(provider);
	}

	@Override
	protected void loadAdditional(ValueInput inputData) {
		super.loadAdditional(inputData);
		input = loadStack(inputData, "Input");
		fuel = loadStack(inputData, "Fuel");
		primaryOutput = loadStack(inputData, "PrimaryOutput");
		secondaryOutput = loadStack(inputData, "SecondaryOutput");
		progress = inputData.getIntOr("Progress", 0);
		maxProgress = inputData.getIntOr("MaxProgress", 0);
		burnTicks = inputData.getIntOr("BurnTicks", 0);
		maxBurnTicks = inputData.getIntOr("MaxBurnTicks", 0);
		autoCrankCooldown = inputData.getIntOr("AutoCrankCooldown", 0);
	}

	private static ItemStack loadStack(ValueInput inputData, String key) {
		return inputData.read(key, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
	}

	private record ProcessingRecipe(ItemStack primaryOutput, ItemStack secondaryOutput, int time) {
	}

	enum StationType {
		EXTRACTOR,
		GRINDER,
		WASHING,
		NONE
	}
}
