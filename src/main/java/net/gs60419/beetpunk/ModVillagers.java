package net.gs60419.beetpunk;

import com.google.common.collect.ImmutableSet;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Block;

public final class ModVillagers {
	private static final Identifier BEET_TRADER_ID = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_trader");
	private static final ResourceKey<PoiType> BEET_TRADER_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, BEET_TRADER_ID);

	public static final PoiType BEET_TRADER_POI = PoiHelper.register(BEET_TRADER_ID, 1, 1, ModBlocks.BEET_TRADING_TABLE);
	public static final VillagerProfession BEET_TRADER = Registry.register(
			BuiltInRegistries.VILLAGER_PROFESSION,
			BEET_TRADER_ID,
			new VillagerProfession(
					Component.translatable("entity.minecraft.villager.beetpunk.beet_trader"),
					poi -> poi.is(BEET_TRADER_POI_KEY),
					poi -> poi.is(BEET_TRADER_POI_KEY),
					requestedItems(),
					ImmutableSet.<Block>of(),
					SoundEvents.VILLAGER_WORK_CLERIC,
					tradeSets()));

	private ModVillagers() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk villagers.");
	}

	private static ImmutableSet<Item> requestedItems() {
		return ImmutableSet.of(
				Items.BEETROOT,
				ModItems.BEET_LEAF,
				ModItems.BEET_CLOTH,
				ModItems.BEET_OIL,
				ModItems.BEET_CRYSTAL_GRAIN);
	}

	private static Int2ObjectMap<ResourceKey<TradeSet>> tradeSets() {
		Int2ObjectOpenHashMap<ResourceKey<TradeSet>> trades = new Int2ObjectOpenHashMap<>();
		for (int level = 1; level <= 5; level++) {
			trades.put(level, ResourceKey.create(Registries.TRADE_SET,
					Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_trader/level_" + level)));
		}
		return trades;
	}
}
