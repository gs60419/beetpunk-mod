package net.gs60419.beetpunk;

import net.fabricmc.fabric.api.registry.CompostableRegistry;

public final class ModCompostables {
	private ModCompostables() {
	}

	public static void register() {
		CompostableRegistry.INSTANCE.add(ModItems.BEET_LEAF, 0.3F);
		CompostableRegistry.INSTANCE.add(ModItems.BEET_FIBER, 0.5F);
		CompostableRegistry.INSTANCE.add(ModItems.BEET_RESIDUE, 0.65F);
		CompostableRegistry.INSTANCE.add(ModBlocks.BEET_SOIL, 0.85F);
		Beetpunk.LOGGER.info("Registering Beetpunk compostables.");
	}
}
