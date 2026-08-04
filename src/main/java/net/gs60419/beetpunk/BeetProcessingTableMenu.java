package net.gs60419.beetpunk;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BeetProcessingTableMenu extends AbstractContainerMenu {
	private static final int TABLE_SLOT_COUNT = 3;
	private static final int INPUT_SLOT = 0;
	private static final int FIRST_OUTPUT_SLOT = 1;
	private static final int DATA_COUNT = 2;

	private final Container container;
	private final ContainerData data;

	public BeetProcessingTableMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(TABLE_SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
	}

	public BeetProcessingTableMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
		super(ModMenus.BEET_PROCESSING_TABLE, containerId);
		checkContainerSize(container, TABLE_SLOT_COUNT);
		checkContainerDataCount(data, DATA_COUNT);
		this.container = container;
		this.data = data;

		container.startOpen(playerInventory.player);
		addSlot(new Slot(container, INPUT_SLOT, 44, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return BeetProcessingTableBlockEntity.isProcessable(stack);
			}
		});
		addSlot(new OutputSlot(container, 1, 104, 35));
		addSlot(new OutputSlot(container, 2, 128, 35));
		addPlayerInventory(playerInventory);
		addDataSlots(data);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (!slot.hasItem()) {
			return result;
		}

		ItemStack stack = slot.getItem();
		result = stack.copy();

		if (index >= FIRST_OUTPUT_SLOT && index < TABLE_SLOT_COUNT) {
			if (!moveItemStackTo(stack, TABLE_SLOT_COUNT, slots.size(), true)) {
				return ItemStack.EMPTY;
			}
			slot.onQuickCraft(stack, result);
		} else if (index == INPUT_SLOT) {
			if (!moveItemStackTo(stack, TABLE_SLOT_COUNT, slots.size(), false)) {
				return ItemStack.EMPTY;
			}
		} else if (BeetProcessingTableBlockEntity.isProcessable(stack)) {
			if (!moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}

		if (stack.getCount() == result.getCount()) {
			return ItemStack.EMPTY;
		}

		slot.onTake(player, stack);
		return result;
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		container.stopOpen(player);
	}

	public int getScaledProgress(int width) {
		int progress = data.get(0);
		int maxProgress = data.get(1);
		if (progress == 0 || maxProgress == 0) {
			return 0;
		}

		return progress * width / maxProgress;
	}

	private void addPlayerInventory(Inventory playerInventory) {
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
			}
		}

		for (int column = 0; column < 9; column++) {
			addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
		}
	}

	private static class OutputSlot extends Slot {
		private OutputSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
	}
}
