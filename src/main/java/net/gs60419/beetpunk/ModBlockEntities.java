package net.gs60419.beetpunk;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public final class ModBlockEntities {
	public static final BlockEntityType<BeetProcessingTableBlockEntity> BEET_PROCESSING_TABLE = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_processing_table"),
		FabricBlockEntityTypeBuilder.create(BeetProcessingTableBlockEntity::new,
			ModBlocks.BEET_PROCESSING_TABLE,
			ModBlocks.BEET_GRINDER_TABLE,
			ModBlocks.BEET_WASHING_TABLE,
			ModBlocks.BEET_EXTRACTOR_BARREL,
			ModBlocks.BEET_GRINDER_BARREL,
			ModBlocks.BEET_WASHING_BARREL).build()
	);
	public static final BlockEntityType<BeetCrankBaseBlockEntity> BEET_CRANK_BASE = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_crank_base"),
		FabricBlockEntityTypeBuilder.create(BeetCrankBaseBlockEntity::new, ModBlocks.BEET_CRANK_BASE).build()
	);
	public static final BlockEntityType<BeetSprinklerBlockEntity> BEET_SPRINKLER = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_sprinkler"),
		FabricBlockEntityTypeBuilder.create(BeetSprinklerBlockEntity::new, ModBlocks.BEET_SPRINKLER).build()
	);
	public static final BlockEntityType<BeetHarvestBoxBlockEntity> BEET_HARVEST_BOX = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_harvest_box"),
		FabricBlockEntityTypeBuilder.create(BeetHarvestBoxBlockEntity::new, ModBlocks.BEET_HARVEST_BOX).build()
	);
	public static final BlockEntityType<BeetTempleCoreBlockEntity> BEET_TEMPLE_CORE = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_temple_core"),
		FabricBlockEntityTypeBuilder.create(BeetTempleCoreBlockEntity::new,
			ModBlocks.SEED_TEMPLE_CORE,
			ModBlocks.SOIL_TEMPLE_CORE,
			ModBlocks.SPROUT_TEMPLE_CORE,
			ModBlocks.WATER_TEMPLE_CORE,
			ModBlocks.GROWTH_TEMPLE_CORE,
			ModBlocks.LIGHT_TEMPLE_CORE,
			ModBlocks.LEAF_TEMPLE_CORE,
			ModBlocks.ROOT_TEMPLE_CORE,
			ModBlocks.BEET_TEMPLE_CORE,
			ModBlocks.SOUP_TEMPLE_CORE,
			ModBlocks.DYE_TEMPLE_CORE,
			ModBlocks.PIG_TEMPLE_CORE,
			ModBlocks.SUPREME_TEMPLE_CORE).build()
	);

	private ModBlockEntities() {
	}

	public static void register() {
		addVanillaBlockEntityBlock(BlockEntityTypes.SIGN, ModBlocks.BEET_SIGN);
		addVanillaBlockEntityBlock(BlockEntityTypes.SIGN, ModBlocks.BEET_WALL_SIGN);
		addVanillaBlockEntityBlock(BlockEntityTypes.HANGING_SIGN, ModBlocks.BEET_HANGING_SIGN);
		addVanillaBlockEntityBlock(BlockEntityTypes.HANGING_SIGN, ModBlocks.BEET_WALL_HANGING_SIGN);
		addVanillaBlockEntityBlock(BlockEntityTypes.CAMPFIRE, ModBlocks.BEET_CAMPFIRE);
		Beetpunk.LOGGER.info("Registering Beetpunk block entities.");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void addVanillaBlockEntityBlock(BlockEntityType<?> type, Block block) {
		((FabricBlockEntityType) type).addValidBlock(block);
	}
}
