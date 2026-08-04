package net.gs60419.beetpunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public final class ModDyeGlyphDrops {
	private static final float DYE_GLYPH_CHANCE = 0.20F;
	private static final Map<UUID, InventorySnapshot> SNAPSHOTS = new HashMap<>();
	private static final Item RED_DYE = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace("red_dye"));

	private ModDyeGlyphDrops() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				checkPlayer(player);
			}
		});
		Beetpunk.LOGGER.info("Registering Beetpunk dye glyph drops.");
	}

	private static void checkPlayer(ServerPlayer player) {
		InventorySnapshot previous = SNAPSHOTS.get(player.getUUID());
		InventorySnapshot current = new InventorySnapshot(count(player, RED_DYE), count(player, Items.BEETROOT));
		SNAPSHOTS.put(player.getUUID(), current);

		if (previous == null) {
			return;
		}

		int redDyeGained = current.redDyeCount() - previous.redDyeCount();
		int beetrootSpent = previous.beetrootCount() - current.beetrootCount();
		int attempts = Math.min(redDyeGained, beetrootSpent);
		if (attempts <= 0 || !ModAdvancements.isDone(player, BeetTempleTier.SOUP.sealTempleAdvancementId())) {
			return;
		}

		for (int index = 0; index < attempts; index++) {
			if (player.level().getRandom().nextFloat() < DYE_GLYPH_CHANCE) {
				Block.popResource(player.level(), player.blockPosition(), new ItemStack(ModItems.DYE_GLYPH));
			}
		}

		applyDyeTempleBonus(player, attempts);
	}

	private static void applyDyeTempleBonus(ServerPlayer player, int attempts) {
		int templeLevel = TempleEffects.nearbyTempleLevel(player.level(), player.blockPosition(), ModBlocks.DYE_TEMPLE_CORE);
		if (templeLevel <= 0) {
			return;
		}

		int bonus = 0;
		for (int index = 0; index < attempts; index++) {
			if (templeLevel >= 1 && player.level().getRandom().nextFloat() < 0.35F) {
				bonus++;
			}
			if (templeLevel >= 2) {
				bonus++;
			}
			if (templeLevel >= 3 && player.level().getRandom().nextFloat() < 0.50F) {
				bonus++;
			}
		}

		if (bonus <= 0) {
			return;
		}

		ItemStack stack = new ItemStack(RED_DYE, bonus);
		if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL && ModHarvestBoxes.tryInsertNearby(player.level(), player.blockPosition(), stack)) {
			return;
		}
		if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL && player.getInventory().add(stack)) {
			return;
		}
		Block.popResource(player.level(), player.blockPosition(), stack);
	}

	private static int count(ServerPlayer player, Item item) {
		int count = 0;
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (stack.is(item)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	private record InventorySnapshot(int redDyeCount, int beetrootCount) {
	}
}
