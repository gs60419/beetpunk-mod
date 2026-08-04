package net.gs60419.beetpunk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;

public class BeetpunkClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.BEET_PROCESSING_TABLE, BeetProcessingTableScreen::new);
		MenuScreens.register(ModMenus.BEET_CRANK_BASE, BeetCrankBaseScreen::new);
		MenuScreens.register(ModMenus.BEET_TEMPLE_CORE, BeetTempleCoreScreen::new);
		BlockEntityRendererRegistry.register(ModBlockEntities.BEET_PROCESSING_TABLE, BeetPrayerBarrelRenderer::new);
		BlockEntityRendererRegistry.register(ModBlockEntities.BEET_TEMPLE_CORE, BeetTempleCoreRenderer::new);
		EntityRendererRegistry.register(ModEntityTypes.BEET_RAFT, context -> new BeetRaftRenderer(context, ModelLayers.BAMBOO_RAFT, false));
		EntityRendererRegistry.register(ModEntityTypes.BEET_CHEST_RAFT, context -> new BeetRaftRenderer(context, ModelLayers.BAMBOO_CHEST_RAFT, true));
		ClientPlayNetworking.registerGlobalReceiver(BeetPilgrimBookPayload.TYPE, (payload, context) ->
			context.client().setScreenAndShow(new BeetPilgrimBookScreen(payload.glyphMask(), payload.sealMask()))
		);
		Beetpunk.LOGGER.info("Registering Beetpunk client screens for 26.2.");
	}
}
