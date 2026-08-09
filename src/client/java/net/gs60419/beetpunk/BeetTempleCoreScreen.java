package net.gs60419.beetpunk;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BeetTempleCoreScreen extends AbstractContainerScreen<BeetTempleCoreMenu> {
	private static final int PANEL_COLOR = 0xFF6E2534;
	private static final int INNER_COLOR = 0xFF201115;
	private static final int SLOT_COLOR = 0xFFC98A91;
	private static final int FILLED_COLOR = 0xFFE05B61;
	private static final int EMPTY_COLOR = 0xFF3E2529;
	private static final int TEXT_COLOR = 0xFFEFD7D7;

	public BeetTempleCoreScreen(BeetTempleCoreMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, 166);
		inventoryLabelY = 72;
	}

	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		int x = leftPos;
		int y = topPos;
		graphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
		graphics.fill(x + 4, y + 4, x + imageWidth - 4, y + imageHeight - 4, INNER_COLOR);
		for (int slot = 0; slot < 4; slot++) {
			drawSlot(graphics, x + 52 + slot * 18, y + 34);
			graphics.centeredText(font, String.valueOf(slot + 1), x + 61 + slot * 18, y + 57, TEXT_COLOR);
		}
		drawLevelBar(graphics, x + 52, y + 24);
		drawInventorySlots(graphics, x + 7, y + 83);
		drawHotbarSlots(graphics, x + 7, y + 141);
		super.extractContents(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, TEXT_COLOR, false);
		graphics.text(font, "LV " + menu.coreLevel() + " / 4", 122, titleLabelY, TEXT_COLOR, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_COLOR, false);
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		if (isCoreSlot(hoveredSlot)) {
			graphics.setComponentTooltipForNextFrame(font, coreSlotTooltip(hoveredSlot.index), mouseX, mouseY);
			return;
		}
		super.extractTooltip(graphics, mouseX, mouseY);
	}

	private void drawLevelBar(GuiGraphicsExtractor graphics, int x, int y) {
		for (int index = 0; index < 4; index++) {
			int color = index < menu.coreLevel() ? FILLED_COLOR : EMPTY_COLOR;
			graphics.fill(x + index * 18, y, x + 16 + index * 18, y + 5, color);
		}
	}

	private static void drawSlot(GuiGraphicsExtractor graphics, int x, int y) {
		graphics.fill(x, y, x + 18, y + 18, SLOT_COLOR);
		graphics.fill(x + 1, y + 1, x + 17, y + 17, INNER_COLOR);
	}

	private static void drawInventorySlots(GuiGraphicsExtractor graphics, int x, int y) {
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				drawSlot(graphics, x + column * 18, y + row * 18);
			}
		}
	}

	private static void drawHotbarSlots(GuiGraphicsExtractor graphics, int x, int y) {
		for (int column = 0; column < 9; column++) {
			drawSlot(graphics, x + column * 18, y);
		}
	}

	private static boolean isCoreSlot(Slot slot) {
		return slot != null && slot.index >= 0 && slot.index < 4;
	}

	private List<Component> coreSlotTooltip(int slotIndex) {
		List<Component> lines = new ArrayList<>();
		int level = slotIndex + 1;
		lines.add(Component.translatable("screen.beetpunk.temple_core.slot", level));
		lines.add(Component.translatable("screen.beetpunk.temple_core.requires", level));
		if (level <= menu.coreLevel()) {
			lines.add(Component.translatable("screen.beetpunk.temple_core.active"));
		} else {
			lines.add(Component.translatable("screen.beetpunk.temple_core.inactive"));
		}
		lines.add(Component.translatable("screen.beetpunk.temple_core.effect." + menu.tier().path() + "." + level));
		return lines;
	}
}
