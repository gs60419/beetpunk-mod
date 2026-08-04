package net.gs60419.beetpunk;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class ModAdvancements {
	public static final Identifier FIRST_HAND_HARVEST = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "first_hand_harvest");
	public static final Identifier FIND_SEED_GLYPH = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "find_seed_glyph");
	public static final Identifier USE_PILGRIM_STAFF = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "use_pilgrim_staff");
	public static final Identifier SEAL_SEED_TEMPLE = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "seal_seed_temple");
	public static final Identifier EMPOWER_SOIL = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "empower_soil");

	private ModAdvancements() {
	}

	public static void award(ServerPlayer player, Identifier id, String criterion) {
		AdvancementHolder advancement = player.level().getServer().getAdvancements().get(id);
		if (advancement != null) {
			player.getAdvancements().award(advancement, criterion);
		}
	}

	public static boolean isDone(ServerPlayer player, Identifier id) {
		AdvancementHolder advancement = player.level().getServer().getAdvancements().get(id);
		return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
	}
}
