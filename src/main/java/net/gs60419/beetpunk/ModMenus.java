package net.gs60419.beetpunk;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
	public static final MenuType<BeetProcessingTableMenu> BEET_PROCESSING_TABLE = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_processing_table"),
		new MenuType<>(BeetProcessingTableMenu::new, FeatureFlags.VANILLA_SET)
	);
	public static final MenuType<BeetCrankBaseMenu> BEET_CRANK_BASE = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_crank_base"),
		new MenuType<>(BeetCrankBaseMenu::new, FeatureFlags.VANILLA_SET)
	);
	public static final MenuType<BeetTempleCoreMenu> BEET_TEMPLE_CORE = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_temple_core"),
		new MenuType<>(BeetTempleCoreMenu::new, FeatureFlags.VANILLA_SET)
	);

	private ModMenus() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk menus.");
	}
}
