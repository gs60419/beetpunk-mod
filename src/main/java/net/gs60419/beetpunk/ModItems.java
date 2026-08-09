package net.gs60419.beetpunk;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.FoodOnAStickItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public final class ModItems {
	public static final Item BEET_RATION = register("beet_ration",
			properties -> new BeetRationItem(properties.food(
					new FoodProperties.Builder()
							.nutrition(7)
							.saturationModifier(0.8f)
							.build())));
	public static final Item ENRICHED_BEET_RATION = register("enriched_beet_ration",
			properties -> new EnrichedBeetRationItem(properties.food(
					new FoodProperties.Builder()
							.nutrition(9)
							.saturationModifier(0.85f)
							.build())));

	public static final Item BEET_LEAF = register("beet_leaf", Item::new);
	public static final Item BEET_STICK = register("beet_stick", Item::new);
	public static final Item BEET_FIBER = register("beet_fiber", BeetFiberItem::new);
	public static final Item BEET_CLOTH = register("beet_cloth", Item::new);
	public static final Item BEET_FILTER = register("beet_filter", Item::new);
	public static final Item BEET_RESIDUE = register("beet_residue", Item::new);
	public static final Item BEET_OIL = register("beet_oil", Item::new);
	public static final Item BEET_WATER_DROP = register("beet_water_drop", Item::new);
	public static final Item BEET_WATER = register("beet_water", BeetWaterItem::new);
	public static final Item BEET_INK = register("beet_ink", Item::new);
	public static final Item BEET_INK_II = register("beet_ink_ii", Item::new);
	public static final Item BEET_INK_III = register("beet_ink_iii", Item::new);
	public static final Item BEET_INK_IV = register("beet_ink_iv", Item::new);
	public static final Item BEET_FERTILIZER = register("beet_fertilizer", BeetFertilizerItem::new);
	public static final Item BEET_IRON_DUST = register("beet_iron_dust", Item::new);
	public static final Item BEET_IRON_INGOT = register("beet_iron_ingot", Item::new);
	public static final Item BEET_REDSTONE_DUST = register("beet_redstone_dust", Item::new);
	public static final Item BEET_CRYSTAL_GRAIN = register("beet_crystal_grain", Item::new);
	public static final Item BEET_WIRE = register("beet_wire", Item::new);
	public static final Item BEET_WOODEN_BUCKET = register("beet_wooden_bucket", properties -> new BeetWoodenBucketItem(false, properties.stacksTo(16)));
	public static final Item BEET_WOODEN_WATER_BUCKET = register("beet_wooden_water_bucket", properties -> new BeetWoodenBucketItem(true, properties.stacksTo(1)));
	public static final Item BEET_RAFT = register("beet_raft", properties -> new BeetRaftItem(false, properties.stacksTo(1)));
	public static final Item BEET_CHEST_RAFT = register("beet_chest_raft", properties -> new BeetRaftItem(true, properties.stacksTo(1)));
	public static final Item BEET_SIGN = register("beet_sign", properties -> new SignItem(ModBlocks.BEET_SIGN, ModBlocks.BEET_WALL_SIGN, properties.stacksTo(16)));
	public static final Item BEET_HANGING_SIGN = register("beet_hanging_sign", properties -> new HangingSignItem(ModBlocks.BEET_HANGING_SIGN, ModBlocks.BEET_WALL_HANGING_SIGN, properties.stacksTo(16)));

	public static final Item SEED_GLYPH = register("seed_glyph", Item::new);
	public static final Item SOIL_GLYPH = register("soil_glyph", Item::new);
	public static final Item SPROUT_GLYPH = register("sprout_glyph", Item::new);
	public static final Item WATER_GLYPH = register("water_glyph", Item::new);
	public static final Item GROWTH_GLYPH = register("growth_glyph", Item::new);
	public static final Item LIGHT_GLYPH = register("light_glyph", Item::new);
	public static final Item LEAF_GLYPH = register("leaf_glyph", Item::new);
	public static final Item ROOT_GLYPH = register("root_glyph", Item::new);
	public static final Item BEET_GLYPH = register("beet_glyph", Item::new);
	public static final Item SOUP_GLYPH = register("soup_glyph", Item::new);
	public static final Item DYE_GLYPH = register("dye_glyph", Item::new);
	public static final Item PIG_GLYPH = register("pig_glyph", Item::new);
	public static final Item SUPREME_GLYPH = register("supreme_glyph", Item::new);
	public static final Item BEET_REVELATION = register("beet_revelation", Item::new);

	public static final Item SEED_SCRIPTURE = registerScripture("seed_scripture", false);
	public static final Item SOIL_SCRIPTURE = registerScripture("soil_scripture", false);
	public static final Item SPROUT_SCRIPTURE = registerScripture("sprout_scripture", false);
	public static final Item WATER_SCRIPTURE = registerScripture("water_scripture", false);
	public static final Item GROWTH_SCRIPTURE = registerScripture("growth_scripture", false);
	public static final Item LIGHT_SCRIPTURE = registerScripture("light_scripture", false);
	public static final Item LEAF_SCRIPTURE = registerScripture("leaf_scripture", false);
	public static final Item ROOT_SCRIPTURE = registerScripture("root_scripture", false);
	public static final Item BEET_SCRIPTURE = registerScripture("beet_scripture", false);
	public static final Item SOUP_SCRIPTURE = registerScripture("soup_scripture", false);
	public static final Item DYE_SCRIPTURE = registerScripture("dye_scripture", false);
	public static final Item PIG_SCRIPTURE = registerScripture("pig_scripture", false);
	public static final Item SUPREME_SCRIPTURE = registerScripture("supreme_scripture", false);
	public static final Item[] SCRIPTURES_II = registerScriptureLevel("scripture_ii", false);
	public static final Item[] SCRIPTURES_III = registerScriptureLevel("scripture_iii", false);
	public static final Item[] SCRIPTURES_IV = registerScriptureLevel("scripture_iv", true);

	public static final Item BEET_PILGRIM_BOOK = register("beet_pilgrim_book", properties -> new BeetPilgrimBookItem(properties.stacksTo(1)));
	public static final Item BEET_PILGRIM_STAFF = register("beet_pilgrim_staff", properties -> new BeetPilgrimStaffItem(properties.stacksTo(1).durability(512)));
	public static final Item BEET_ON_A_STICK = register("beet_on_a_stick", properties -> new FoodOnAStickItem<>(pigEntityType(), 7, properties.stacksTo(1).durability(25)));
	public static final Item BEET_WOODEN_SWORD = register("beet_wooden_sword", properties -> new Item(toolProperties(properties).sword(ToolMaterial.WOOD, 3, -2.4F)));
	public static final Item BEET_WOODEN_SHOVEL = register("beet_wooden_shovel", properties -> new ShovelItem(ToolMaterial.WOOD, 1.5F, -3.0F, toolProperties(properties)));
	public static final Item BEET_WOODEN_PICKAXE = register("beet_wooden_pickaxe", properties -> new Item(toolProperties(properties).pickaxe(ToolMaterial.WOOD, 1.0F, -2.8F)));
	public static final Item BEET_WOODEN_AXE = register("beet_wooden_axe", properties -> new AxeItem(ToolMaterial.WOOD, 6.0F, -3.2F, toolProperties(properties)));
	public static final Item BEET_WOODEN_HOE = register("beet_wooden_hoe", properties -> new HoeItem(ToolMaterial.WOOD, 0.0F, -3.0F, toolProperties(properties)));
	public static final Item BEET_WOODEN_BOW = register("beet_wooden_bow", properties -> new BowItem(properties.durability(384)));
	public static final Item BEET_IRON_SWORD = register("beet_iron_sword", properties -> new Item(toolProperties(properties).sword(ToolMaterial.IRON, 3, -2.4F)));
	public static final Item BEET_IRON_SHOVEL = register("beet_iron_shovel", properties -> new ShovelItem(ToolMaterial.IRON, 1.5F, -3.0F, toolProperties(properties)));
	public static final Item BEET_IRON_PICKAXE = register("beet_iron_pickaxe", properties -> new Item(toolProperties(properties).pickaxe(ToolMaterial.IRON, 1.0F, -2.8F)));
	public static final Item BEET_IRON_AXE = register("beet_iron_axe", properties -> new AxeItem(ToolMaterial.IRON, 6.0F, -3.1F, toolProperties(properties)));
	public static final Item BEET_IRON_HOE = register("beet_iron_hoe", properties -> new HoeItem(ToolMaterial.IRON, -2.0F, -1.0F, toolProperties(properties)));
	public static final Item BEET_IRON_HELMET = register("beet_iron_helmet", properties -> new Item(armorProperties(properties, ArmorType.HELMET)));
	public static final Item BEET_IRON_CHESTPLATE = register("beet_iron_chestplate", properties -> new Item(armorProperties(properties, ArmorType.CHESTPLATE)));
	public static final Item BEET_IRON_LEGGINGS = register("beet_iron_leggings", properties -> new Item(armorProperties(properties, ArmorType.LEGGINGS)));
	public static final Item BEET_IRON_BOOTS = register("beet_iron_boots", properties -> new Item(armorProperties(properties, ArmorType.BOOTS)));

	private ModItems() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk items.");
	}

	private static Item register(String path, java.util.function.Function<Item.Properties, Item> itemFactory) {
		Identifier id = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path);
		return Registry.register(BuiltInRegistries.ITEM, id, itemFactory.apply(properties(id)));
	}

	private static Item registerScripture(String path, boolean foil) {
		return register(path, properties -> new BeetScriptureItem(foil, properties));
	}

	private static Item[] registerScriptureLevel(String suffix, boolean foil) {
		BeetTempleTier[] tiers = BeetTempleTier.values();
		Item[] scriptures = new Item[tiers.length];
		for (int index = 0; index < tiers.length; index++) {
			scriptures[index] = registerScripture(tiers[index].path() + "_" + suffix, foil);
		}
		return scriptures;
	}

	private static Item.Properties properties(Identifier id) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
	}

	private static Item.Properties toolProperties(Item.Properties properties) {
		return properties.stacksTo(1);
	}

	private static Item.Properties armorProperties(Item.Properties properties, ArmorType type) {
		return properties.humanoidArmor(ModArmorMaterials.BEET_IRON, type);
	}

	@SuppressWarnings("unchecked")
	private static EntityType<Pig> pigEntityType() {
		return (EntityType<Pig>) BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace("pig"));
	}

	public static Item glyphFor(BeetTempleTier tier) {
		return switch (tier) {
			case SEED -> SEED_GLYPH;
			case SOIL -> SOIL_GLYPH;
			case SPROUT -> SPROUT_GLYPH;
			case WATER -> WATER_GLYPH;
			case GROWTH -> GROWTH_GLYPH;
			case LIGHT -> LIGHT_GLYPH;
			case LEAF -> LEAF_GLYPH;
			case ROOT -> ROOT_GLYPH;
			case BEET -> BEET_GLYPH;
			case SOUP -> SOUP_GLYPH;
			case DYE -> DYE_GLYPH;
			case PIG -> PIG_GLYPH;
			case SUPREME -> SUPREME_GLYPH;
		};
	}

	public static Item scriptureFor(BeetTempleTier tier) {
		return scriptureFor(tier, 1);
	}

	public static Item scriptureFor(BeetTempleTier tier, int level) {
		if (level == 2) {
			return SCRIPTURES_II[tier.ordinal()];
		}
		if (level == 3) {
			return SCRIPTURES_III[tier.ordinal()];
		}
		if (level == 4) {
			return SCRIPTURES_IV[tier.ordinal()];
		}
		return switch (tier) {
			case SEED -> SEED_SCRIPTURE;
			case SOIL -> SOIL_SCRIPTURE;
			case SPROUT -> SPROUT_SCRIPTURE;
			case WATER -> WATER_SCRIPTURE;
			case GROWTH -> GROWTH_SCRIPTURE;
			case LIGHT -> LIGHT_SCRIPTURE;
			case LEAF -> LEAF_SCRIPTURE;
			case ROOT -> ROOT_SCRIPTURE;
			case BEET -> BEET_SCRIPTURE;
			case SOUP -> SOUP_SCRIPTURE;
			case DYE -> DYE_SCRIPTURE;
			case PIG -> PIG_SCRIPTURE;
			case SUPREME -> SUPREME_SCRIPTURE;
		};
	}
}
