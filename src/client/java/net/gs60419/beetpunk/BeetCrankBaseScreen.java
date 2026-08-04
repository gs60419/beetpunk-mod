package net.gs60419.beetpunk;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BeetCrankBaseScreen extends AbstractContainerScreen<BeetCrankBaseMenu> {
	private static final int PANEL_COLOR = 0xFF6E2534;
	private static final int INNER_COLOR = 0xFF201115;
	private static final int SLOT_COLOR = 0xFFC98A91;
	private static final int FLAME_EMPTY = 0xFF3E2529;
	private static final int FLAME_FILLED = 0xFFFF7A35;
	private static final int PROGRESS_EMPTY = 0xFF3E2529;
	private static final int PROGRESS_FILLED = 0xFFE05B61;
	private static final int TEXT_COLOR = 0xFFEFD7D7;

	public BeetCrankBaseScreen(BeetCrankBaseMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, 176, 166);
		inventoryLabelY = 72;
	}

	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		int x = leftPos;
		int y = topPos;
		graphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL_COLOR);
		graphics.fill(x + 4, y + 4, x + imageWidth - 4, y + imageHeight - 4, INNER_COLOR);
		drawSlot(graphics, x + 43, y + 23);
		drawSlot(graphics, x + 43, y + 54);
		drawSlot(graphics, x + 115, y + 34);
		drawSlot(graphics, x + 139, y + 34);
		drawFlame(graphics, x + 47, y + 43);
		drawProgress(graphics, x + 69, y + 38);
		drawInventorySlots(graphics, x + 7, y + 83);
		drawHotbarSlots(graphics, x + 7, y + 141);
		super.extractContents(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, TEXT_COLOR, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_COLOR, false);
	}

	private void drawFlame(GuiGraphicsExtractor graphics, int x, int y) {
		graphics.fill(x, y, x + 10, y + 9, FLAME_EMPTY);
		int filled = menu.getScaledBurn(7);
		if (filled > 0) {
			graphics.fill(x + 1, y + 8 - filled, x + 9, y + 8, FLAME_FILLED);
		}
	}

	private void drawProgress(GuiGraphicsExtractor graphics, int x, int y) {
		graphics.fill(x, y, x + 34, y + 10, PROGRESS_EMPTY);
		graphics.fill(x + 1, y + 1, x + 1 + menu.getScaledProgress(32), y + 9, PROGRESS_FILLED);
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
