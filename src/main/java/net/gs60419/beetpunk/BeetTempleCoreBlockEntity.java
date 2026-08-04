package net.gs60419.beetpunk;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BeetTempleCoreBlockEntity extends BlockEntity implements Container, MenuProvider {
	public static final int SLOT_COUNT = 4;
	private final NonNullList<ItemStack> scriptures = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

	public BeetTempleCoreBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BEET_TEMPLE_CORE, pos, state);
	}

	public int level() {
		int level = 0;
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			if (scriptures.get(slot).isEmpty()) {
				break;
			}
			level++;
		}
		return level;
	}

	public boolean insert(ItemStack stack, int scriptureLevel) {
		int slot = scriptureLevel - 1;
		if (slot < 0 || slot >= SLOT_COUNT || !scriptures.get(slot).isEmpty()) {
			return false;
		}

		scriptures.set(slot, stack.copyWithCount(1));
		setChanged();
		syncGlow();
		return true;
	}

	public ItemStack removeLast() {
		for (int slot = SLOT_COUNT - 1; slot >= 0; slot--) {
			ItemStack stack = scriptures.get(slot);
			if (!stack.isEmpty()) {
				scriptures.set(slot, ItemStack.EMPTY);
				setChanged();
				syncGlow();
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public List<ItemStack> removeAll() {
		List<ItemStack> stacks = new ArrayList<>();
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			ItemStack stack = scriptures.get(slot);
			if (!stack.isEmpty()) {
				stacks.add(stack);
				scriptures.set(slot, ItemStack.EMPTY);
			}
		}
		setChanged();
		syncGlow();
		return stacks;
	}

	public BeetTempleTier tier() {
		if (getBlockState().getBlock() instanceof BeetTempleCoreBlock block) {
			return block.tier();
		}
		return BeetTempleTier.SEED;
	}

	public int expectedScriptureLevel(ItemStack stack) {
		if (getBlockState().getBlock() instanceof BeetTempleCoreBlock block) {
			return block.scriptureLevel(stack);
		}
		return 0;
	}

	public boolean canInsertAt(int slot, ItemStack stack) {
		if (slot < 0 || slot >= SLOT_COUNT || !scriptures.get(slot).isEmpty()) {
			return false;
		}
		return expectedScriptureLevel(stack) == slot + 1 && level() == slot;
	}

	@Override
	public Component getDisplayName() {
		return Component.literal(tier().displayName() + "神殿核心");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new BeetTempleCoreMenu(containerId, playerInventory, this);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ValueOutput.ValueOutputList list = output.childrenList("Scriptures");
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			ItemStack stack = scriptures.get(slot);
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
		scriptures.replaceAll(stack -> ItemStack.EMPTY);
		for (ValueInput stackData : input.childrenListOrEmpty("Scriptures")) {
			int slot = stackData.getByteOr("Slot", (byte) -1);
			if (slot >= 0 && slot < SLOT_COUNT) {
				scriptures.set(slot, stackData.read("Stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
			}
		}
	}

	@Override
	public int getContainerSize() {
		return SLOT_COUNT;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : scriptures) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot < 0 || slot >= SLOT_COUNT) {
			return ItemStack.EMPTY;
		}
		return scriptures.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack result = stack.split(amount);
		if (stack.isEmpty()) {
			scriptures.set(slot, ItemStack.EMPTY);
		}
		setChanged();
		syncGlow();
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		if (slot >= 0 && slot < SLOT_COUNT) {
			scriptures.set(slot, ItemStack.EMPTY);
			syncGlow();
		}
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot < 0 || slot >= SLOT_COUNT) {
			return;
		}
		ItemStack limitedStack = stack.copy();
		if (limitedStack.getCount() > getMaxStackSize()) {
			limitedStack.setCount(getMaxStackSize());
		}
		scriptures.set(slot, limitedStack);
		setChanged();
		syncGlow();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return canInsertAt(slot, stack);
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
		scriptures.replaceAll(stack -> ItemStack.EMPTY);
		setChanged();
		syncGlow();
	}

	private void syncGlow() {
		if (level != null) {
			BeetTempleCoreBlock.syncGlow(getBlockState(), level, worldPosition, level());
		}
	}
}
