package net.gs60419.beetpunk;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ModItemGroups {
	public static final CreativeModeTab BEETPUNK = Registry.register(
		BuiltInRegistries.CREATIVE_MODE_TAB,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beetpunk"),
		FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(Items.BEETROOT))
			.title(Component.translatable("itemGroup.beetpunk"))
			.displayItems((context, entries) -> {
				entries.accept(ModItems.BEET_RATION);
				entries.accept(ModItems.ENRICHED_BEET_RATION);
				entries.accept(ModItems.BEET_LEAF);
				entries.accept(ModItems.BEET_STICK);
				entries.accept(ModItems.BEET_FIBER);
				entries.accept(ModItems.BEET_CLOTH);
				entries.accept(ModBlocks.BEET_CLOTH_BLOCK);
				entries.accept(ModBlocks.BEET_BED);
				entries.accept(ModItems.BEET_FILTER);
				entries.accept(ModItems.BEET_RESIDUE);
				entries.accept(ModItems.BEET_OIL);
				entries.accept(ModItems.BEET_WATER_DROP);
				entries.accept(ModItems.BEET_WATER);
				entries.accept(ModItems.BEET_INK);
				entries.accept(ModItems.BEET_INK_II);
				entries.accept(ModItems.BEET_INK_III);
				entries.accept(ModItems.BEET_INK_IV);
				entries.accept(ModItems.BEET_FERTILIZER);
				entries.accept(ModItems.BEET_IRON_DUST);
				entries.accept(ModItems.BEET_IRON_INGOT);
				entries.accept(ModItems.BEET_REDSTONE_DUST);
				entries.accept(ModItems.BEET_CRYSTAL_GRAIN);
				entries.accept(ModItems.BEET_WIRE);
				entries.accept(ModItems.BEET_WOODEN_BUCKET);
				entries.accept(ModItems.BEET_WOODEN_WATER_BUCKET);
				entries.accept(ModItems.BEET_RAFT);
				entries.accept(ModItems.BEET_CHEST_RAFT);
				entries.accept(ModItems.BEET_SIGN);
				entries.accept(ModItems.BEET_HANGING_SIGN);
				entries.accept(ModItems.BEET_REVELATION);
				for (BeetTempleTier tier : BeetTempleTier.values()) {
					entries.accept(ModItems.glyphFor(tier));
				}
				for (BeetTempleTier tier : BeetTempleTier.values()) {
					entries.accept(ModItems.scriptureFor(tier));
				}
				for (int level = 2; level <= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL; level++) {
					for (BeetTempleTier tier : BeetTempleTier.values()) {
						entries.accept(ModItems.scriptureFor(tier, level));
					}
				}
				entries.accept(ModItems.BEET_PILGRIM_BOOK);
				entries.accept(ModItems.BEET_PILGRIM_STAFF);
				entries.accept(ModItems.BEET_ON_A_STICK);
				entries.accept(ModItems.BEET_WOODEN_SWORD);
				entries.accept(ModItems.BEET_WOODEN_SHOVEL);
				entries.accept(ModItems.BEET_WOODEN_PICKAXE);
				entries.accept(ModItems.BEET_WOODEN_AXE);
				entries.accept(ModItems.BEET_WOODEN_HOE);
				entries.accept(ModItems.BEET_WOODEN_BOW);
				entries.accept(ModItems.BEET_IRON_SWORD);
				entries.accept(ModItems.BEET_IRON_SHOVEL);
				entries.accept(ModItems.BEET_IRON_PICKAXE);
				entries.accept(ModItems.BEET_IRON_AXE);
				entries.accept(ModItems.BEET_IRON_HOE);
				entries.accept(ModItems.BEET_IRON_HELMET);
				entries.accept(ModItems.BEET_IRON_CHESTPLATE);
				entries.accept(ModItems.BEET_IRON_LEGGINGS);
				entries.accept(ModItems.BEET_IRON_BOOTS);
				entries.accept(ModBlocks.BEET_PLANK_BLOCK);
				entries.accept(ModBlocks.BEET_STALK_BLOCK);
				entries.accept(ModBlocks.STRIPPED_BEET_STALK_BLOCK);
				entries.accept(ModBlocks.BEET_PLANK_STAIRS);
				entries.accept(ModBlocks.BEET_PLANK_SLAB);
				entries.accept(ModBlocks.BEET_PLANK_FENCE);
				entries.accept(ModBlocks.BEET_PLANK_FENCE_GATE);
				entries.accept(ModBlocks.BEET_PLANK_DOOR);
				entries.accept(ModBlocks.BEET_PLANK_TRAPDOOR);
				entries.accept(ModBlocks.BEET_PLANK_PRESSURE_PLATE);
				entries.accept(ModBlocks.BEET_PLANK_BUTTON);
				entries.accept(ModBlocks.BEET_BLOCK);
				entries.accept(ModBlocks.DRIED_BEET_BLOCK);
				entries.accept(ModBlocks.DRIED_BEET_SLAB);
				entries.accept(ModBlocks.BEET_BRICK_BLOCK);
				entries.accept(ModBlocks.CHISELED_BEET_BRICK);
				entries.accept(ModBlocks.BEET_BRICK_STAIRS);
				entries.accept(ModBlocks.BEET_BRICK_SLAB);
				entries.accept(ModBlocks.BEET_BRICK_WALL);
				entries.accept(ModBlocks.BEET_COBBLESTONE);
				entries.accept(ModBlocks.BEET_COBBLESTONE_STAIRS);
				entries.accept(ModBlocks.BEET_COBBLESTONE_SLAB);
				entries.accept(ModBlocks.BEET_COBBLESTONE_WALL);
				entries.accept(ModBlocks.BEET_GRAVEL);
				entries.accept(ModBlocks.BEET_SAND);
				entries.accept(ModBlocks.BEET_SOIL);
				entries.accept(ModBlocks.BEET_FARMLAND);
				entries.accept(ModBlocks.FERTILIZED_BEET_SOIL);
				entries.accept(ModBlocks.FERTILIZED_BEET_FARMLAND);
				entries.accept(ModBlocks.BEET_CRANK_BASE);
				entries.accept(ModBlocks.BEET_EXTRACTOR_BARREL);
				entries.accept(ModBlocks.BEET_GRINDER_BARREL);
				entries.accept(ModBlocks.BEET_WASHING_BARREL);
				entries.accept(ModBlocks.BEET_HARVEST_BOX);
				entries.accept(ModBlocks.BEET_SPRINKLER);
				entries.accept(ModBlocks.BEET_TRADING_TABLE);
				entries.accept(ModBlocks.BEET_TORCH);
				entries.accept(ModBlocks.BEET_CAMPFIRE);
				entries.accept(ModBlocks.BEET_GEOTHERMAL_CORE);
				for (BeetTempleTier tier : BeetTempleTier.values()) {
					entries.accept(ModBlocks.templeCoreFor(tier));
				}
			})
			.build()
	);

	private ModItemGroups() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk item group.");
	}
}
