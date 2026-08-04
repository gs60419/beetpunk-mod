package net.gs60419.beetpunk;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BeetTempleCoreMenu extends AbstractContainerMenu {
	private static final int CORE_SLOT_COUNT = BeetTempleCoreBlockEntity.SLOT_COUNT;

	private final Container container;
	private final Player player;
	private int lastLevel;

	public BeetTempleCoreMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(CORE_SLOT_COUNT));
	}

	public BeetTempleCoreMenu(int containerId, Inventory playerInventory, Container container) {
		super(ModMenus.BEET_TEMPLE_CORE, containerId);
		checkContainerSize(container, CORE_SLOT_COUNT);
		this.container = container;
		this.player = playerInventory.player;
		this.lastLevel = currentLevel();

		container.startOpen(playerInventory.player);
		for (int slot = 0; slot < CORE_SLOT_COUNT; slot++) {
			addSlot(new ScriptureSlot(container, slot, 53 + slot * 18, 35));
		}
		addPlayerInventory(playerInventory);
	}

	public int coreLevel() {
		return currentLevel();
	}

	public BeetTempleTier tier() {
		if (container instanceof BeetTempleCoreBlockEntity core) {
			return core.tier();
		}
		return BeetTempleTier.SEED;
	}

	@Override
	public void slotsChanged(Container changedContainer) {
		super.slotsChanged(changedContainer);
		int level = currentLevel();
		if (level > lastLevel && player instanceof ServerPlayer serverPlayer && container instanceof BeetTempleCoreBlockEntity core) {
			ModAdvancements.award(serverPlayer, core.tier().sealTempleAdvancementId(), "sealed");
			if (core.tier() == BeetTempleTier.PIG
					&& level >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL
					&& lastLevel < BeetTempleCoreBlock.MAX_TEMPLE_LEVEL
					&& hasAllTwelveTempleSeals(serverPlayer)) {
				Block.popResource(serverPlayer.level(), serverPlayer.blockPosition(), new ItemStack(ModItems.SUPREME_GLYPH));
			}
		}
		lastLevel = level;
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

		if (index < CORE_SLOT_COUNT) {
			if (!moveItemStackTo(stack, CORE_SLOT_COUNT, slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else if (!moveToCore(stack)) {
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

	private boolean moveToCore(ItemStack stack) {
		for (int slot = 0; slot < CORE_SLOT_COUNT; slot++) {
			if (container.canPlaceItem(slot, stack) && moveItemStackTo(stack, slot, slot + 1, false)) {
				return true;
			}
		}
		return false;
	}

	private int currentLevel() {
		if (container instanceof BeetTempleCoreBlockEntity core) {
			return core.level();
		}
		int level = 0;
		for (int slot = 0; slot < CORE_SLOT_COUNT; slot++) {
			if (container.getItem(slot).isEmpty()) {
				break;
			}
			level++;
		}
		return level;
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

	private static class ScriptureSlot extends Slot {
		private final int templeSlot;

		private ScriptureSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
			this.templeSlot = slot;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return container.canPlaceItem(templeSlot, stack);
		}

		@Override
		public int getMaxStackSize() {
			return 1;
		}
	}
}
