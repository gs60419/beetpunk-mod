package net.gs60419.beetpunk;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BeetProcessingTableScreen extends AbstractContainerScreen<BeetProcessingTableMenu> {
	private static final int PANEL_COLOR = 0xFF7A3140;
	private static final int INNER_COLOR = 0xFF2B171B;
	private static final int SLOT_COLOR = 0xFFB77A81;
	private static final int PROGRESS_EMPTY = 0xFF3E2529;
	private static final int PROGRESS_FILLED = 0xFFE05B61;

	public BeetProcessingTableScreen(BeetProcessingTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, 166);
		inventoryLabelY = 72;
	}

	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		int x = leftPos;
		int y = topPos;
		graphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
		graphics.fill(x + 4, y + 4, x + imageWidth - 4, y + imageHeight - 4, INNER_COLOR);
		drawSlot(graphics, x + 43, y + 34);
		drawSlot(graphics, x + 103, y + 34);
		drawSlot(graphics, x + 127, y + 34);
		drawProgress(graphics, x + 66, y + 38);
		drawInventorySlots(graphics, x + 7, y + 83);
		drawHotbarSlots(graphics, x + 7, y + 141);
		super.extractContents(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, 0xFFEFD7D7, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFEFD7D7, false);
	}

	private void drawProgress(GuiGraphicsExtractor graphics, int x, int y) {
		graphics.fill(x, y, x + 26, y + 10, PROGRESS_EMPTY);
		graphics.fill(x + 1, y + 1, x + 1 + menu.getScaledProgress(24), y + 9, PROGRESS_FILLED);
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
}
