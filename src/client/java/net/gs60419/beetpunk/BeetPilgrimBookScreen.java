package net.gs60419.beetpunk;

import java.util.List;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BeetPilgrimBookScreen extends Screen {
	private static final int BOOK_WIDTH = 260;
	private static final int BOOK_HEIGHT = 176;
	private static final int TAB_WIDTH = 48;
	private static final int TAB_HEIGHT = 22;
	private static final int PAGE_COLOR = 0xFFFFF9E6;
	private static final int PAGE_EDGE = 0xFF6E2534;
	private static final int PAGE_SHADOW = 0x99210F16;
	private static final int PAGE_FOLD = 0xFFE8D2A0;
	private static final int TEXT_COLOR = 0xFF24130F;
	private static final int MUTED_TEXT = 0xFF4F352F;
	private static final int TITLE_COLOR = 0xFF6E2534;
	private static final int ACTIVE_TAB = 0xFFC94A61;
	private static final int INACTIVE_TAB = 0xFF8A4C55;

	private final int glyphMask;
	private final int sealMask;
	private Page page = Page.GUIDE;
	private int tierPage;

	protected BeetPilgrimBookScreen(int glyphMask, int sealMask) {
		super(Component.literal("甜菜巡禮帳"));
		this.glyphMask = glyphMask;
		this.sealMask = sealMask;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean isInGameUi() {
		return true;
	}

	@Override
	public void extractTransparentBackground(GuiGraphicsExtractor graphics) {
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		int x = bookX();
		int y = bookY();
		drawBook(graphics, x, y);
		drawTabs(graphics, x, y);
		drawPage(graphics, x, y);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();
		int x = bookX();
		int y = bookY();
		for (Page candidate : Page.values()) {
			int tabY = y + 18 + candidate.ordinal() * (TAB_HEIGHT + 4);
			if (mouseX >= x - TAB_WIDTH + 6 && mouseX < x && mouseY >= tabY && mouseY < tabY + TAB_HEIGHT) {
				page = candidate;
				return true;
			}
		}
		if (page == Page.PILGRIMAGE) {
			if (mouseX >= x + 151 && mouseX < x + 171 && mouseY >= y + 147 && mouseY < y + 165) {
				tierPage = Math.max(0, tierPage - 1);
				return true;
			}
			if (mouseX >= x + 215 && mouseX < x + 235 && mouseY >= y + 147 && mouseY < y + 165) {
				tierPage = Math.min(BeetTempleTier.values().length - 1, tierPage + 1);
				return true;
			}
		}
		return super.mouseClicked(event, doubleClick);
	}

	private void drawBook(GuiGraphicsExtractor graphics, int x, int y) {
		graphics.fill(x + 5, y + 6, x + BOOK_WIDTH + 5, y + BOOK_HEIGHT + 6, PAGE_SHADOW);
		graphics.fill(x, y, x + BOOK_WIDTH, y + BOOK_HEIGHT, PAGE_EDGE);
		graphics.fill(x + 5, y + 5, x + BOOK_WIDTH - 5, y + BOOK_HEIGHT - 5, PAGE_COLOR);
		graphics.fill(x + BOOK_WIDTH / 2 - 1, y + 8, x + BOOK_WIDTH / 2 + 1, y + BOOK_HEIGHT - 8, PAGE_FOLD);
	}

	private void drawTabs(GuiGraphicsExtractor graphics, int x, int y) {
		for (Page candidate : Page.values()) {
			int tabX = x - TAB_WIDTH + 6;
			int tabY = y + 18 + candidate.ordinal() * (TAB_HEIGHT + 4);
			boolean active = candidate == page;
			graphics.fill(tabX, tabY, x + 2, tabY + TAB_HEIGHT, active ? ACTIVE_TAB : INACTIVE_TAB);
			graphics.fill(tabX + 2, tabY + 2, x + 2, tabY + TAB_HEIGHT - 2, active ? 0xFFE27B84 : 0xFFA66768);
			graphics.text(font, candidate.label, tabX + 8, tabY + 7, 0xFFFFFFFF, false);
		}
	}

	private void drawPage(GuiGraphicsExtractor graphics, int x, int y) {
		switch (page) {
			case GUIDE -> drawGuide(graphics, x, y);
			case MATERIALS -> drawMaterials(graphics, x, y);
			case PILGRIMAGE -> drawPilgrimagePage(graphics, x, y);
		}
	}

	private void drawGuide(GuiGraphicsExtractor graphics, int x, int y) {
		drawSection(graphics, x + 18, y + 24, 104, "路線手冊", List.of(
			"收成熟甜菜會取得原版甜菜，並額外掉落甜菜葉。",
			"空手收成有低機率取得萌種聖文字。",
			"巡禮帳記錄玩家進度，遺失重做仍會同步。"
		));
		drawSection(graphics, x + 140, y + 24, 102, "神殿循環", List.of(
			"聖文字加甜菜布可裝訂經書。",
			"經書、甜菜磚、甜菜晶粒可合成神殿核心。",
			"啟動核心後，用巡禮帳右鍵取得該階朱印。"
		));
	}

	private void drawMaterials(GuiGraphicsExtractor graphics, int x, int y) {
		drawSection(graphics, x + 18, y + 20, 104, "材料衍生", List.of(
			"1. 纖維、布料、羊毛體系：甜菜葉衍生。",
			"2. 木材體系：甜菜葉轉甜菜枝條衍生。",
			"3. 石頭體系：甜菜塊脫水後的甜菜石塊衍生。",
			"4. 特殊礦物體系：甜菜石塊粉碎、篩洗衍生。"
		));
		drawSection(graphics, x + 140, y + 24, 102, "三台作業", List.of(
			"榨取台：榨出甜菜水滴、甜菜油與甜菜渣。",
			"研磨台：甜菜石塊走向鵝卵、礫石與甜菜沙。",
			"篩洗台：用濾網與水洗出鐵砂等礦物材料。",
			"高階土壤支撐農業與自動化。"
		));
	}

	private void drawPilgrimagePage(GuiGraphicsExtractor graphics, int x, int y) {
		BeetTempleTier tier = BeetTempleTier.values()[tierPage];
		boolean glyphDone = has(glyphMask, tierPage);
		boolean sealDone = has(sealMask, tierPage);
		graphics.text(font, String.format("%02d  %s神殿", tierPage + 1, tier.displayName()), x + 20, y + 24, TEXT_COLOR, false);
		drawSection(graphics, x + 20, y + 40, 100, "聖文字取得", glyphText(tier));
		drawSection(graphics, x + 20, y + 102, 100, "蓋章與活化", List.of(
			"合成該階經書與核心。",
			"將經書放入核心後，用巡禮帳右鍵蓋章。",
			"4 本經書補滿即 LV4。"
		));

		drawCenteredPlain(graphics, tier.displayName() + "御朱印", x + 191, y + 32, TEXT_COLOR);
		drawGoshuinEmblem(graphics, x + 151, y + 45, tier, glyphDone, sealDone);
		drawCenteredPlain(graphics, emblemStatus(glyphDone, sealDone), x + 191, y + 126,
				sealDone ? TITLE_COLOR : glyphDone ? TEXT_COLOR : MUTED_TEXT);

		drawPageButton(graphics, x + 151, y + 147, "<", tierPage > 0);
		drawCenteredPlain(graphics, (tierPage + 1) + " / " + BeetTempleTier.values().length, x + 193, y + 153, MUTED_TEXT);
		drawPageButton(graphics, x + 215, y + 147, ">", tierPage < BeetTempleTier.values().length - 1);
	}

	private void drawGoshuinEmblem(GuiGraphicsExtractor graphics, int x, int y, BeetTempleTier tier, boolean glyphDone, boolean sealDone) {
		graphics.fill(x + 3, y + 3, x + 77, y + 81, 0x16A06A5F);
		graphics.fill(x + 4, y + 5, x + 76, y + 77, 0xFFFFFFFF);
		if (!glyphDone && !sealDone) {
			graphics.fill(x + 17, y + 17, x + 63, y + 63, 0x18A06A5F);
			return;
		}
		if (glyphDone) {
			drawTotemTexture(graphics, x + 4, y + 5, tier, "glyph");
		}
		if (sealDone) {
			drawTotemTexture(graphics, x + 4, y + 5, tier, "temple");
		}
	}

	private void drawTotemTexture(GuiGraphicsExtractor graphics, int x, int y, BeetTempleTier tier, String layer) {
		Identifier texture = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID,
				"textures/gui/pilgrim_book/totems/ui/" + tier.path() + "_" + layer + ".png");
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, 72, 72, 72, 72);
	}

	private String emblemStatus(boolean glyphDone, boolean sealDone) {
		if (sealDone) {
			return "墨書與朱印完成";
		}
		return glyphDone ? "已有聖文字墨書" : "尚未取得";
	}

	private void drawPageButton(GuiGraphicsExtractor graphics, int x, int y, String label, boolean enabled) {
		graphics.fill(x, y, x + 20, y + 18, enabled ? ACTIVE_TAB : INACTIVE_TAB);
		drawCenteredPlain(graphics, label, x + 10, y + 5, enabled ? 0xFFFFFFFF : 0xFFB8A6A6);
	}

	private void drawCenteredPlain(GuiGraphicsExtractor graphics, String text, int centerX, int y, int color) {
		graphics.text(font, text, centerX - font.width(text) / 2, y, color, false);
	}

	private List<String> glyphText(BeetTempleTier tier) {
		return switch (tier) {
			case SEED -> List.of("種下甜菜種子時低機率出現。", "最早的甜菜啟蒙。");
			case SOIL -> List.of("甜菜土與耕地路線相關。", "穩定農地的第一步。");
			case SPROUT -> List.of("催熟與壯苗路線相關。", "讓作物更容易推進。");
			case WATER -> List.of("甜菜水滴與甜菜水路線相關。", "支撐灑水與微催熟。");
			case GROWTH -> List.of("肥料與高階土壤路線相關。", "強化農業循環。");
			case LIGHT -> List.of("甜菜油、火把與營火路線相關。", "讓基地獲得特殊照明。");
			case LEAF -> List.of("甜菜葉、纖維、布料路線相關。", "打開纖維與羊毛體系。");
			case ROOT -> List.of("根系、榨取與磚材路線相關。", "把甜菜推向建材。");
			case BEET -> List.of("甜菜本體與乾糧路線相關。", "整理主要甜菜產物。");
			case SOUP -> List.of("甜菜湯與食物路線相關。", "補上生存消耗。");
			case DYE -> List.of("甜菜轉紅色染料時觸發。", "朱印與墨水的色彩來源。");
			case PIG -> List.of("餵豬與聖豬神殿相關。", "空島可用天啟生成第一隻豬。");
			case SUPREME -> List.of("完成聖豬 LV4 後取得。", "通往村民與至高循環。");
		};
	}

	private static boolean has(int mask, int index) {
		return (mask & (1 << index)) != 0;
	}

	private void drawSection(GuiGraphicsExtractor graphics, int x, int y, int width, String heading, List<String> lines) {
		graphics.text(font, heading, x, y, TEXT_COLOR, false);
		int lineY = y + 16;
		for (String line : lines) {
			Component component = Component.literal(line);
			graphics.textWithWordWrap(font, component, x, lineY, width, MUTED_TEXT, false);
			lineY += 10 * Math.max(1, font.split(component, width).size()) + 4;
		}
	}

	private int bookX() {
		return (width - BOOK_WIDTH) / 2 + 18;
	}

	private int bookY() {
		return (height - BOOK_HEIGHT) / 2;
	}

	private enum Page {
		GUIDE("導覽"),
		MATERIALS("材料"),
		PILGRIMAGE("巡禮");

		private final String label;

		Page(String label) {
			this.label = label;
		}
	}
}
