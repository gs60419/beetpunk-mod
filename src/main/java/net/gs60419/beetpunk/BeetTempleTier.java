package net.gs60419.beetpunk;

import net.minecraft.resources.Identifier;

public enum BeetTempleTier {
	SEED("seed", "萌種"),
	SOIL("soil", "沃土"),
	SPROUT("sprout", "初芽"),
	WATER("water", "清水"),
	GROWTH("growth", "壯苗"),
	LIGHT("light", "耀光"),
	LEAF("leaf", "茂葉"),
	ROOT("root", "豐根"),
	BEET("beet", "甜菜"),
	SOUP("soup", "甘湯"),
	DYE("dye", "染彩"),
	PIG("pig", "聖豬"),
	SUPREME("supreme", "至高");

	private final String path;
	private final String displayName;

	BeetTempleTier(String path, String displayName) {
		this.path = path;
		this.displayName = displayName;
	}

	public String path() {
		return path;
	}

	public String displayName() {
		return displayName;
	}

	public Identifier findGlyphAdvancementId() {
		return id("find_" + path + "_glyph");
	}

	public Identifier craftScriptureAdvancementId() {
		return id("craft_" + path + "_scripture");
	}

	public Identifier sealTempleAdvancementId() {
		return id("seal_" + path + "_temple");
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path);
	}
}
