package net.gs60419.beetpunk;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Beetpunk implements ModInitializer {
	public static final String MOD_ID = "beetpunk";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModWoodTypes.register();
		ModBlocks.register();
		ModItems.register();
		ModVillagers.register();
		ModEntityTypes.register();
		ModBlockEntities.register();
		ModMenus.register();
		ModNetworking.registerCommon();
		ModTilling.register();
		ModFuels.register();
		ModCompostables.register();
		ModFlammables.register();
		ModGlyphDrops.register();
		ModDyeGlyphDrops.register();
		ModTempleAmbience.register();
		ModSproutTempleEffects.register();
		ModLightTempleEffects.register();
		ModGeothermalEffects.register();
		ModSupremeTempleEffects.register();
		ModItemGroups.register();

		LOGGER.info("Beetpunk V1 prototype loaded.");
	}
}
