package net.gs60419.beetpunk;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BeetCrankBaseScreen extends AbstractContainerScreen<BeetCrankBaseMenu> {
	private static final int PANEL_COLOR = 0xFF6E2534;
	private static final int INNER_COLOR = 0xFF201115;
	private static final int SLOT_COLOR = 0xFFC98A91;
	private static final int FLAME_EMPTY = 0xFF3E2529;
	private static final int FLAME_FILLED = 0xFFFF7A35;
	private static final int PROGRESS_EMPTY = 0xFF3E2529;
	private static final int PROGRESS_FILLED = 0xFFE05B61;
	private static final int TEXT_COLOR = 0xFFEFD7D7;
	private static final int MUTED_TEXT = 0xFFB89499;
	private static final int RECIPE_PANEL_WIDTH = 116;
	private static final int RECIPE_ROW_HEIGHT = 25;
	private static final int RECIPE_VISIBLE_ROWS = 4;
	private static final int EXTRACTOR_MASK = 1;
	private static final int GRINDER_MASK = 2;
	private static final int WASHING_MASK = 4;
	private static final Map<BeetCrankBaseBlockEntity.StationType, List<RecipeHint>> RECIPE_HINTS = Map.of(
			BeetCrankBaseBlockEntity.StationType.EXTRACTOR, List.of(
					new RecipeHint(new ItemStack(ModBlocks.BEET_BLOCK), new ItemStack(ModBlocks.DRIED_BEET_BLOCK), new ItemStack(ModItems.BEET_WATER_DROP), "榨出水滴"),
					new RecipeHint(new ItemStack(ModBlocks.DRIED_BEET_BLOCK), new ItemStack(ModItems.BEET_RESIDUE), new ItemStack(ModItems.BEET_OIL), "榨出油與渣"),
					new RecipeHint(new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(ModItems.BEET_RESIDUE), new ItemStack(ModItems.BEET_OIL), "種子榨油")
			),
			BeetCrankBaseBlockEntity.StationType.GRINDER, List.of(
					new RecipeHint(new ItemStack(ModBlocks.BEET_BRICK_BLOCK), new ItemStack(ModBlocks.BEET_COBBLESTONE), ItemStack.EMPTY, "磚材破碎"),
					new RecipeHint(new ItemStack(ModBlocks.BEET_COBBLESTONE), new ItemStack(ModBlocks.BEET_GRAVEL), ItemStack.EMPTY, "鵝卵成礫"),
					new RecipeHint(new ItemStack(ModBlocks.BEET_GRAVEL), new ItemStack(ModBlocks.BEET_SAND), new ItemStack(ModItems.BEET_CRYSTAL_GRAIN), "礫石成沙")
			),
			BeetCrankBaseBlockEntity.StationType.WASHING, List.of(
					new RecipeHint(new ItemStack(ModBlocks.BEET_SAND), new ItemStack(ModItems.BEET_IRON_DUST), new ItemStack(Items.CLAY_BALL), "洗出礦物"),
					new RecipeHint(new ItemStack(ModItems.BEET_CRYSTAL_GRAIN), new ItemStack(ModItems.BEET_REDSTONE_DUST), ItemStack.EMPTY, "洗出紅石")
			)
	);

	private boolean recipesOpen = true;
	private int selectedRecipe;
	private int recipeScroll;

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
		drawRecipeToggle(graphics, x, y);
		if (recipesOpen) {
			drawRecipePanel(graphics, mouseX, mouseY, recipePanelX(), y);
		}
		super.extractContents(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(font, title, titleLabelX, titleLabelY, TEXT_COLOR, false);
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_COLOR, false);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();
		int toggleX = leftPos + imageWidth - 18;
		int toggleY = topPos + 6;
		if (inside(mouseX, mouseY, toggleX, toggleY, 12, 12)) {
			recipesOpen = !recipesOpen;
			return true;
		}

		if (recipesOpen) {
			int panelX = recipePanelX();
			int panelY = topPos;
			if (inside(mouseX, mouseY, panelX + 94, panelY + 18, 12, 12)) {
				scrollRecipes(-1);
				return true;
			}
			if (inside(mouseX, mouseY, panelX + 94, panelY + 139, 12, 12)) {
				scrollRecipes(1);
				return true;
			}

			List<RecipeHint> recipes = visibleRecipes();
			for (int i = 0; i < Math.min(RECIPE_VISIBLE_ROWS, recipes.size() - recipeScroll); i++) {
				if (inside(mouseX, mouseY, panelX + 8, panelY + 34 + i * RECIPE_ROW_HEIGHT, 96, 22)) {
					selectedRecipe = recipeScroll + i;
					return true;
				}
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	private void drawRecipeToggle(GuiGraphicsExtractor graphics, int x, int y) {
		int toggleX = x + imageWidth - 18;
		int toggleY = y + 6;
		graphics.fill(toggleX, toggleY, toggleX + 12, toggleY + 12, recipesOpen ? 0xFFE05B61 : SLOT_COLOR);
		graphics.fill(toggleX + 1, toggleY + 1, toggleX + 11, toggleY + 11, INNER_COLOR);
		graphics.text(font, "?", toggleX + 4, toggleY + 2, TEXT_COLOR, false);
	}

	private int recipePanelX() {
		return leftPos - RECIPE_PANEL_WIDTH - 4;
	}

	private void drawRecipePanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
		graphics.fill(x, y, x + RECIPE_PANEL_WIDTH, y + imageHeight, PANEL_COLOR);
		graphics.fill(x + 4, y + 4, x + RECIPE_PANEL_WIDTH - 4, y + imageHeight - 4, INNER_COLOR);
		graphics.text(font, "配方書", x + 10, y + 10, TEXT_COLOR, false);
		graphics.text(font, stationTitle(menu.stationType()), x + 10, y + 22, MUTED_TEXT, false);

		List<RecipeHint> recipes = visibleRecipes();
		if (recipes.isEmpty()) {
			graphics.text(font, "上方放入桶身", x + 12, y + 48, MUTED_TEXT, false);
			graphics.text(font, "即可顯示配方。", x + 12, y + 60, MUTED_TEXT, false);
			return;
		}

		selectedRecipe = Math.max(0, Math.min(selectedRecipe, recipes.size() - 1));
		recipeScroll = Math.max(0, Math.min(recipeScroll, Math.max(0, recipes.size() - RECIPE_VISIBLE_ROWS)));
		drawScrollButton(graphics, x + 94, y + 18, "^", recipeScroll > 0);
		drawScrollButton(graphics, x + 94, y + 139, "v", recipeScroll + RECIPE_VISIBLE_ROWS < recipes.size());

		for (int i = 0; i < Math.min(RECIPE_VISIBLE_ROWS, recipes.size() - recipeScroll); i++) {
			int recipeIndex = recipeScroll + i;
			RecipeHint recipe = recipes.get(recipeIndex);
			int rowY = y + 34 + i * RECIPE_ROW_HEIGHT;
			boolean selected = recipeIndex == selectedRecipe;
			graphics.fill(x + 8, rowY, x + 104, rowY + 22, selected ? 0xFF7F3343 : 0xFF3A1A21);
			graphics.fakeItem(recipe.input(), x + 10, rowY + 3);
			graphics.text(font, ">", x + 31, rowY + 7, MUTED_TEXT, false);
			graphics.fakeItem(recipe.primaryOutput(), x + 43, rowY + 3);
			if (!recipe.secondaryOutput().isEmpty()) {
				graphics.text(font, "+", x + 62, rowY + 7, MUTED_TEXT, false);
				graphics.fakeItem(recipe.secondaryOutput(), x + 72, rowY + 3);
			}
			if (inside(mouseX, mouseY, x + 8, rowY, 96, 22)) {
				graphics.setComponentTooltipForNextFrame(font, recipe.tooltip(hasInput(recipe.input())), mouseX, mouseY);
			}
		}

		RecipeHint selected = recipes.get(selectedRecipe);
		graphics.text(font, selected.note(), x + 10, y + 136, TEXT_COLOR, false);
		graphics.text(font, hasInput(selected.input()) ? "背包已有材料" : "缺少輸入材料", x + 10, y + 149,
				hasInput(selected.input()) ? 0xFF8FE07D : 0xFFFFA0A0, false);
	}

	private void drawScrollButton(GuiGraphicsExtractor graphics, int x, int y, String label, boolean enabled) {
		graphics.fill(x, y, x + 12, y + 12, enabled ? SLOT_COLOR : 0xFF54343A);
		graphics.text(font, label, x + 3, y + 2, enabled ? TEXT_COLOR : MUTED_TEXT, false);
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

	private List<RecipeHint> visibleRecipes() {
		if (menu.stationType() == BeetCrankBaseBlockEntity.StationType.UNIVERSAL) {
			List<RecipeHint> recipes = new ArrayList<>();
			int mask = menu.barrelTypeMask();
			if ((mask & EXTRACTOR_MASK) != 0) {
				recipes.addAll(RECIPE_HINTS.getOrDefault(BeetCrankBaseBlockEntity.StationType.EXTRACTOR, List.of()));
			}
			if ((mask & GRINDER_MASK) != 0) {
				recipes.addAll(RECIPE_HINTS.getOrDefault(BeetCrankBaseBlockEntity.StationType.GRINDER, List.of()));
			}
			if ((mask & WASHING_MASK) != 0) {
				recipes.addAll(RECIPE_HINTS.getOrDefault(BeetCrankBaseBlockEntity.StationType.WASHING, List.of()));
			}
			return recipes;
		}
		return RECIPE_HINTS.getOrDefault(menu.stationType(), List.of());
	}

	private void scrollRecipes(int delta) {
		List<RecipeHint> recipes = visibleRecipes();
		recipeScroll = Math.max(0, Math.min(recipeScroll + delta, Math.max(0, recipes.size() - RECIPE_VISIBLE_ROWS)));
		selectedRecipe = Math.max(recipeScroll, Math.min(selectedRecipe, Math.min(recipes.size() - 1, recipeScroll + RECIPE_VISIBLE_ROWS - 1)));
	}

	private boolean hasInput(ItemStack input) {
		for (int i = 0; i < minecraft.player.getInventory().getContainerSize(); i++) {
			if (minecraft.player.getInventory().getItem(i).is(input.getItem())) {
				return true;
			}
		}
		return false;
	}

	private static String stationTitle(BeetCrankBaseBlockEntity.StationType type) {
		return switch (type) {
			case EXTRACTOR -> "榨取桶";
			case GRINDER -> "研磨桶";
			case WASHING -> "篩洗桶";
			case UNIVERSAL -> "萬能筒組";
			case NONE -> "未安裝桶身";
		};
	}

	private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	private record RecipeHint(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, String note) {
		private List<Component> tooltip(boolean hasInput) {
			return List.of(
					Component.literal(input.getHoverName().getString()),
					Component.literal("輸出：" + primaryOutput.getHoverName().getString()
							+ (secondaryOutput.isEmpty() ? "" : " + " + secondaryOutput.getHoverName().getString())),
					Component.literal(hasInput ? "背包已有輸入材料" : "背包缺少輸入材料")
			);
		}
	}
}
