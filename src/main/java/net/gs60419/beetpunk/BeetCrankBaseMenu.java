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

public class BeetCrankBaseMenu extends AbstractContainerMenu {
	private static final int BASE_SLOT_COUNT = 4;
	private static final int INPUT_SLOT = 0;
	private static final int FUEL_SLOT = 1;
	private static final int FIRST_OUTPUT_SLOT = 2;
	private static final int DATA_COUNT = 6;

	private final Container container;
	private final ContainerData data;

	public BeetCrankBaseMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(BASE_SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
	}

	public BeetCrankBaseMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
		super(ModMenus.BEET_CRANK_BASE, containerId);
		checkContainerSize(container, BASE_SLOT_COUNT);
		checkContainerDataCount(data, DATA_COUNT);
		this.container = container;
		this.data = data;

		container.startOpen(playerInventory.player);
		addSlot(new InputSlot(container, INPUT_SLOT, 44, 24));
		addSlot(new FuelSlot(container, FUEL_SLOT, 44, 55));
		addSlot(new OutputSlot(container, 2, 116, 35));
		addSlot(new OutputSlot(container, 3, 140, 35));
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

		if (index >= FIRST_OUTPUT_SLOT && index < BASE_SLOT_COUNT) {
			if (!moveItemStackTo(stack, BASE_SLOT_COUNT, slots.size(), true)) {
				return ItemStack.EMPTY;
			}
			slot.onQuickCraft(stack, result);
		} else if (index < BASE_SLOT_COUNT) {
			if (!moveItemStackTo(stack, BASE_SLOT_COUNT, slots.size(), false)) {
				return ItemStack.EMPTY;
			}
		} else if (BeetProcessingTableBlockEntity.isProcessable(stack)) {
			if (!moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (ModFuels.fuelTicks(stack) > 0) {
			if (!moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false)) {
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
		if (progress <= 0 || maxProgress <= 0) {
			return 0;
		}
		return progress * width / maxProgress;
	}

	public int getScaledBurn(int height) {
		int burn = data.get(2);
		int maxBurn = data.get(3);
		if (burn <= 0 || maxBurn <= 0) {
			return 0;
		}
		return burn * height / maxBurn;
	}

	public BeetCrankBaseBlockEntity.StationType stationType() {
		int ordinal = data.get(4);
		BeetCrankBaseBlockEntity.StationType[] values = BeetCrankBaseBlockEntity.StationType.values();
		if (ordinal < 0 || ordinal >= values.length) {
			return BeetCrankBaseBlockEntity.StationType.NONE;
		}
		return values[ordinal];
	}

	public int barrelTypeMask() {
		return data.get(5);
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

	private static class InputSlot extends Slot {
		private InputSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return BeetProcessingTableBlockEntity.isProcessable(stack);
		}
	}

	private static class FuelSlot extends Slot {
		private FuelSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return ModFuels.fuelTicks(stack) > 0;
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
