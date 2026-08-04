package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BeetHarvestBoxBlockEntity extends BlockEntity implements Container, MenuProvider {
	public static final int SLOT_COUNT = 27;
	private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

	public BeetHarvestBoxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BEET_HARVEST_BOX, pos, state);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.beetpunk.beet_harvest_box");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return ChestMenu.threeRows(containerId, playerInventory, this);
	}

	public boolean insert(ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}

		ItemStack remaining = stack.copy();
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			ItemStack current = items.get(slot);
			if (!current.isEmpty()
					&& ItemStack.isSameItemSameComponents(current, remaining)
					&& current.getCount() < current.getMaxStackSize()) {
				int moved = Math.min(remaining.getCount(), current.getMaxStackSize() - current.getCount());
				current.grow(moved);
				remaining.shrink(moved);
				if (remaining.isEmpty()) {
					setChanged();
					return true;
				}
			}
		}

		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			if (items.get(slot).isEmpty()) {
				items.set(slot, remaining);
				setChanged();
				return true;
			}
		}
		return false;
	}

	public boolean canAccept(ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}
		for (ItemStack current : items) {
			if (current.isEmpty()
					|| (ItemStack.isSameItemSameComponents(current, stack)
					&& current.getCount() < current.getMaxStackSize())) {
				return true;
			}
		}
		return false;
	}

	public void dropContents(Level level, BlockPos pos) {
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			ItemStack stack = items.get(slot);
			if (!stack.isEmpty()) {
				Block.popResource(level, pos, stack);
				items.set(slot, ItemStack.EMPTY);
			}
		}
		setChanged();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ValueOutput.ValueOutputList list = output.childrenList("Items");
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			ItemStack stack = items.get(slot);
			if (!stack.isEmpty()) {
				ValueOutput stackData = list.addChild();
				stackData.putByte("Slot", (byte) slot);
				stackData.store("Stack", ItemStack.OPTIONAL_CODEC, stack);
			}
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items.replaceAll(stack -> ItemStack.EMPTY);
		for (ValueInput stackData : input.childrenListOrEmpty("Items")) {
			int slot = stackData.getByteOr("Slot", (byte) -1);
			if (slot >= 0 && slot < SLOT_COUNT) {
				items.set(slot, stackData.read("Stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
			}
		}
	}

	@Override
	public int getContainerSize() {
		return SLOT_COUNT;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 0 && slot < SLOT_COUNT ? items.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack result = stack.split(amount);
		if (stack.isEmpty()) {
			items.set(slot, ItemStack.EMPTY);
		}
		setChanged();
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		if (slot >= 0 && slot < SLOT_COUNT) {
			items.set(slot, ItemStack.EMPTY);
		}
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot < 0 || slot >= SLOT_COUNT) {
			return;
		}
		items.set(slot, stack.copy());
		if (items.get(slot).getCount() > getMaxStackSize()) {
			items.get(slot).setCount(getMaxStackSize());
		}
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr(
				worldPosition.getX() + 0.5D,
				worldPosition.getY() + 0.5D,
				worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clearContent() {
		items.replaceAll(stack -> ItemStack.EMPTY);
		setChanged();
	}
}
