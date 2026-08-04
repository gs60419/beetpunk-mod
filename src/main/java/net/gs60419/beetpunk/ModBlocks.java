package net.gs60419.beetpunk;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class ModBlocks {
	public static final Block BEET_PLANK_BLOCK = register("beet_plank_block", new Block(properties("beet_plank_block", BlockBehaviour.Properties.of().strength(2.0F, 3.0F))));
	public static final Block BEET_STALK_BLOCK = register("beet_stalk_block", new RotatedPillarBlock(properties("beet_stalk_block", woodProperties())));
	public static final Block STRIPPED_BEET_STALK_BLOCK = register("stripped_beet_stalk_block", new RotatedPillarBlock(properties("stripped_beet_stalk_block", woodProperties())));
	public static final Block BEET_PLANK_STAIRS = register("beet_plank_stairs", new StairBlock(BEET_PLANK_BLOCK.defaultBlockState(), properties("beet_plank_stairs", woodProperties())));
	public static final Block BEET_PLANK_SLAB = register("beet_plank_slab", new SlabBlock(properties("beet_plank_slab", woodProperties())));
	public static final Block BEET_PLANK_FENCE = register("beet_plank_fence", new FenceBlock(properties("beet_plank_fence", woodProperties())));
	public static final Block BEET_PLANK_FENCE_GATE = register("beet_plank_fence_gate", new FenceGateBlock(ModWoodTypes.BEET, properties("beet_plank_fence_gate", woodProperties())));
	public static final Block BEET_PLANK_DOOR = register("beet_plank_door", new DoorBlock(ModWoodTypes.BEET_SET, properties("beet_plank_door", woodProperties().noOcclusion())));
	public static final Block BEET_PLANK_TRAPDOOR = register("beet_plank_trapdoor", new TrapDoorBlock(ModWoodTypes.BEET_SET, properties("beet_plank_trapdoor", woodProperties().noOcclusion())));
	public static final Block BEET_PLANK_PRESSURE_PLATE = register("beet_plank_pressure_plate", new PressurePlateBlock(ModWoodTypes.BEET_SET, properties("beet_plank_pressure_plate", woodProperties().noCollision())));
	public static final Block BEET_PLANK_BUTTON = register("beet_plank_button", new ButtonBlock(ModWoodTypes.BEET_SET, 30, properties("beet_plank_button", woodProperties().noCollision())));
	public static final Block BEET_SIGN = registerNoItem("beet_sign", new StandingSignBlock(ModWoodTypes.BEET, properties("beet_sign", woodProperties().noCollision())));
	public static final Block BEET_WALL_SIGN = registerNoItem("beet_wall_sign", new WallSignBlock(ModWoodTypes.BEET, properties("beet_wall_sign", woodProperties().noCollision())));
	public static final Block BEET_HANGING_SIGN = registerNoItem("beet_hanging_sign", new CeilingHangingSignBlock(ModWoodTypes.BEET, properties("beet_hanging_sign", woodProperties().noCollision())));
	public static final Block BEET_WALL_HANGING_SIGN = registerNoItem("beet_wall_hanging_sign", new WallHangingSignBlock(ModWoodTypes.BEET, properties("beet_wall_hanging_sign", woodProperties().noCollision())));
	public static final Block BEET_CLOTH_BLOCK = register("beet_cloth_block", new Block(properties("beet_cloth_block", BlockBehaviour.Properties.of().strength(0.8F).sound(SoundType.WOOL).ignitedByLava())));
	public static final Block BEET_BED = register("beet_bed", new BedBlock(DyeColor.MAGENTA, properties("beet_bed", BlockBehaviour.Properties.of().strength(0.2F).sound(SoundType.WOOD).noOcclusion())));
	public static final Block BEET_BLOCK = registerWithCustomItem("beet_block",
			new Block(properties("beet_block", BlockBehaviour.Properties.of().strength(0.8F))),
			(block, itemProperties) -> new RootGlyphBlockItem(block, itemProperties));
	public static final Block DRIED_BEET_BLOCK = register("dried_beet_block", new Block(properties("dried_beet_block", stoneProperties())));
	public static final Block DRIED_BEET_SLAB = register("dried_beet_slab", new SlabBlock(properties("dried_beet_slab", stoneProperties())));
	public static final Block BEET_BRICK_BLOCK = register("beet_brick_block", new Block(properties("beet_brick_block", stoneProperties())));
	public static final Block CHISELED_BEET_BRICK = register("chiseled_beet_brick", new Block(properties("chiseled_beet_brick", stoneProperties())));
	public static final Block BEET_BRICK_STAIRS = register("beet_brick_stairs", new StairBlock(BEET_BRICK_BLOCK.defaultBlockState(), properties("beet_brick_stairs", stoneProperties())));
	public static final Block BEET_BRICK_SLAB = register("beet_brick_slab", new SlabBlock(properties("beet_brick_slab", stoneProperties())));
	public static final Block BEET_BRICK_WALL = register("beet_brick_wall", new WallBlock(properties("beet_brick_wall", stoneProperties())));
	public static final Block BEET_COBBLESTONE = register("beet_cobblestone", new Block(properties("beet_cobblestone", stoneProperties())));
	public static final Block BEET_COBBLESTONE_STAIRS = register("beet_cobblestone_stairs", new StairBlock(BEET_COBBLESTONE.defaultBlockState(), properties("beet_cobblestone_stairs", stoneProperties())));
	public static final Block BEET_COBBLESTONE_SLAB = register("beet_cobblestone_slab", new SlabBlock(properties("beet_cobblestone_slab", stoneProperties())));
	public static final Block BEET_COBBLESTONE_WALL = register("beet_cobblestone_wall", new WallBlock(properties("beet_cobblestone_wall", stoneProperties())));
	public static final Block BEET_GRAVEL = register("beet_gravel", new Block(properties("beet_gravel", BlockBehaviour.Properties.of().strength(0.6F))));
	public static final Block BEET_SAND = register("beet_sand", new Block(properties("beet_sand", BlockBehaviour.Properties.of().strength(0.5F))));
	public static final Block BEET_SOIL = register("beet_soil", new Block(properties("beet_soil", BlockBehaviour.Properties.of().strength(0.5F))));
	public static final Block BEET_FARMLAND = register("beet_farmland", new BeetFarmlandBlock(properties("beet_farmland", BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND))));
	public static final Block FERTILIZED_BEET_SOIL = register("fertilized_beet_soil", new Block(properties("fertilized_beet_soil", BlockBehaviour.Properties.of().strength(0.6F))));
	public static final Block FERTILIZED_BEET_FARMLAND = register("fertilized_beet_farmland", new FertilizedBeetFarmlandBlock(properties("fertilized_beet_farmland", BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND))));
	public static final Block BEET_PROCESSING_TABLE = register("beet_processing_table", new BeetProcessingTableBlock(properties("beet_processing_table", BlockBehaviour.Properties.of().strength(2.5F, 3.0F))));
	public static final Block BEET_GRINDER_TABLE = register("beet_grinder_table", new BeetProcessingTableBlock(properties("beet_grinder_table", BlockBehaviour.Properties.of().strength(2.5F, 3.0F))));
	public static final Block BEET_WASHING_TABLE = register("beet_washing_table", new BeetProcessingTableBlock(properties("beet_washing_table", BlockBehaviour.Properties.of().strength(2.5F, 3.0F))));
	public static final Block BEET_CRANK_BASE = register("beet_crank_base", new BeetCrankBaseBlock(properties("beet_crank_base", woodProperties().strength(2.5F, 3.0F))));
	public static final Block BEET_EXTRACTOR_BARREL = register("beet_extractor_barrel", new BeetProcessingTableBlock(properties("beet_extractor_barrel", BlockBehaviour.Properties.of().strength(2.5F, 3.0F))));
	public static final Block BEET_GRINDER_BARREL = register("beet_grinder_barrel", new BeetProcessingTableBlock(properties("beet_grinder_barrel", BlockBehaviour.Properties.of().strength(2.5F, 3.0F))));
	public static final Block BEET_WASHING_BARREL = register("beet_washing_barrel", new BeetProcessingTableBlock(properties("beet_washing_barrel", BlockBehaviour.Properties.of().strength(2.5F, 3.0F))));
	public static final Block BEET_HARVEST_BOX = register("beet_harvest_box", new BeetHarvestBoxBlock(properties("beet_harvest_box", woodProperties().strength(2.5F, 3.0F))));
	public static final Block BEET_SPRINKLER = register("beet_sprinkler", new BeetSprinklerBlock(properties("beet_sprinkler", woodProperties().strength(2.5F, 3.0F))));
	public static final Block BEET_TRADING_TABLE = register("beet_trading_table", new Block(properties("beet_trading_table", woodProperties().strength(2.5F, 3.0F))));
	public static final Block BEET_WALL_TORCH = registerNoItem("beet_wall_torch", new BeetWallTorchBlock(ParticleTypes.FLAME, properties("beet_wall_torch", BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).lightLevel(state -> 15))));
	public static final Block BEET_TORCH = registerWithCustomItem("beet_torch",
			new BeetTorchBlock(ParticleTypes.FLAME, properties("beet_torch", BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).lightLevel(state -> 15))),
			(block, itemProperties) -> new StandingAndWallBlockItem(block, BEET_WALL_TORCH, Direction.DOWN, itemProperties));
	public static final Block BEET_CAMPFIRE = registerWithCustomItem("beet_campfire",
			new CampfireBlock(true, 2, properties("beet_campfire", BlockBehaviour.Properties.ofFullCopy(Blocks.CAMPFIRE).lightLevel(state -> 15))),
			(block, itemProperties) -> new BeetCampfireBlockItem(block, itemProperties));
	public static final Block BEET_GEOTHERMAL_CORE = register("beet_geothermal_core",
			new Block(properties("beet_geothermal_core", stoneProperties().lightLevel(state -> 10))));
	public static final Block SEED_TEMPLE_CORE = registerTempleCore(BeetTempleTier.SEED);
	public static final Block SOIL_TEMPLE_CORE = registerTempleCore(BeetTempleTier.SOIL);
	public static final Block SPROUT_TEMPLE_CORE = registerTempleCore(BeetTempleTier.SPROUT);
	public static final Block WATER_TEMPLE_CORE = registerTempleCore(BeetTempleTier.WATER);
	public static final Block GROWTH_TEMPLE_CORE = registerTempleCore(BeetTempleTier.GROWTH);
	public static final Block LIGHT_TEMPLE_CORE = registerTempleCore(BeetTempleTier.LIGHT);
	public static final Block LEAF_TEMPLE_CORE = registerTempleCore(BeetTempleTier.LEAF);
	public static final Block ROOT_TEMPLE_CORE = registerTempleCore(BeetTempleTier.ROOT);
	public static final Block BEET_TEMPLE_CORE = registerTempleCore(BeetTempleTier.BEET);
	public static final Block SOUP_TEMPLE_CORE = registerTempleCore(BeetTempleTier.SOUP);
	public static final Block DYE_TEMPLE_CORE = registerTempleCore(BeetTempleTier.DYE);
	public static final Block PIG_TEMPLE_CORE = registerTempleCore(BeetTempleTier.PIG);
	public static final Block SUPREME_TEMPLE_CORE = registerTempleCore(BeetTempleTier.SUPREME);

	private ModBlocks() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk blocks.");
	}

	private static Block register(String path, Block block) {
		Identifier id = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path);
		Block registered = Registry.register(BuiltInRegistries.BLOCK, id, block);
		Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(registered, itemProperties(path)));
		return registered;
	}

	private static Block registerWithCustomItem(String path, Block block, java.util.function.BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
		Identifier id = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path);
		Block registered = Registry.register(BuiltInRegistries.BLOCK, id, block);
		Registry.register(BuiltInRegistries.ITEM, id, itemFactory.apply(registered, itemProperties(path)));
		return registered;
	}

	private static Block registerNoItem(String path, Block block) {
		return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path), block);
	}

	private static Block registerTempleCore(BeetTempleTier tier) {
		String path = tier.path() + "_temple_core";
		return register(path, new BeetTempleCoreBlock(tier,
				properties(path, BlockBehaviour.Properties.of()
						.strength(3.0F, 6.0F)
						.lightLevel(state -> templeCoreLight(state.getValue(BeetTempleCoreBlock.GLOW))))));
	}

	private static int templeCoreLight(int level) {
		return switch (level) {
			case 1 -> 6;
			case 2 -> 9;
			case 3 -> 12;
			default -> level >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 15 : 0;
		};
	}

	private static BlockBehaviour.Properties woodProperties() {
		return BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.BAMBOO_WOOD).ignitedByLava();
	}

	private static BlockBehaviour.Properties stoneProperties() {
		return BlockBehaviour.Properties.of().strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops();
	}

	private static BlockBehaviour.Properties properties(String path, BlockBehaviour.Properties properties) {
		return properties.setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path)));
	}

	private static Item.Properties itemProperties(String path) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path)));
	}

	public static Block templeCoreFor(BeetTempleTier tier) {
		return switch (tier) {
			case SEED -> SEED_TEMPLE_CORE;
			case SOIL -> SOIL_TEMPLE_CORE;
			case SPROUT -> SPROUT_TEMPLE_CORE;
			case WATER -> WATER_TEMPLE_CORE;
			case GROWTH -> GROWTH_TEMPLE_CORE;
			case LIGHT -> LIGHT_TEMPLE_CORE;
			case LEAF -> LEAF_TEMPLE_CORE;
			case ROOT -> ROOT_TEMPLE_CORE;
			case BEET -> BEET_TEMPLE_CORE;
			case SOUP -> SOUP_TEMPLE_CORE;
			case DYE -> DYE_TEMPLE_CORE;
			case PIG -> PIG_TEMPLE_CORE;
			case SUPREME -> SUPREME_TEMPLE_CORE;
		};
	}
}
