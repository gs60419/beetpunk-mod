package net.gs60419.beetpunk;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public final class ModFlammables {
	private ModFlammables() {
	}

	public static void register() {
		FlammableBlockRegistry registry = FlammableBlockRegistry.getDefaultInstance();
		registry.add(ModBlocks.BEET_PLANK_BLOCK, 5, 20);
		registry.add(ModBlocks.BEET_BLOCK, 5, 5);
		registry.add(ModBlocks.DRIED_BEET_BLOCK, 5, 30);
		Beetpunk.LOGGER.info("Registering Beetpunk flammable blocks.");
	}
}
