package net.gs60419.beetpunk;

import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ModFuels {
	public static final int BEET_STICK_TICKS = 100;
	public static final int BEET_PLANK_TICKS = 300;
	public static final int DRIED_BEET_BLOCK_TICKS = 800;
	public static final int BEET_OIL_TICKS = 1600;
	public static final int VANILLA_WOOD_TICKS = 300;
	public static final int VANILLA_CHARCOAL_TICKS = 800;
	public static final int VANILLA_COAL_TICKS = 1600;

	private ModFuels() {
	}

	public static void register() {
		FuelValueEvents.BUILD.register((builder, context) -> {
			builder.add(ModItems.BEET_STICK, BEET_STICK_TICKS);
			builder.add(ModBlocks.BEET_PLANK_BLOCK, BEET_PLANK_TICKS);
			builder.add(ModBlocks.DRIED_BEET_BLOCK, DRIED_BEET_BLOCK_TICKS);
			builder.add(ModItems.BEET_OIL, BEET_OIL_TICKS);
			builder.add(Items.CHARCOAL, VANILLA_CHARCOAL_TICKS);
			builder.add(Items.COAL, VANILLA_COAL_TICKS);
		});
		Beetpunk.LOGGER.info("Registering Beetpunk fuels.");
	}

	public static int fuelTicks(ItemStack stack) {
		if (stack.is(ModItems.BEET_STICK)) {
			return BEET_STICK_TICKS;
		}
		if (stack.is(ModBlocks.BEET_PLANK_BLOCK.asItem())) {
			return BEET_PLANK_TICKS;
		}
		if (stack.is(ModBlocks.DRIED_BEET_BLOCK.asItem())) {
			return DRIED_BEET_BLOCK_TICKS;
		}
		if (stack.is(ModItems.BEET_OIL)) {
			return BEET_OIL_TICKS;
		}
		if (stack.is(Items.COAL)) {
			return VANILLA_COAL_TICKS;
		}
		if (stack.is(Items.CHARCOAL)) {
			return VANILLA_CHARCOAL_TICKS;
		}
		if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.PLANKS)) {
			return VANILLA_WOOD_TICKS;
		}
		return 0;
	}
}
