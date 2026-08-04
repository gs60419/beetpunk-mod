package net.gs60419.beetpunk;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BeetTempleCoreRenderer implements BlockEntityRenderer<BeetTempleCoreBlockEntity, BeetTempleCoreRenderer.State> {
	private static final int BEAM_HEIGHT = 1024;
	private static final int BEET_BEAM_COLOR = 0xFFF15A78;
	private static final int SUPREME_BEAM_COLOR = 0xFFFFE8A8;

	public BeetTempleCoreRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(BeetTempleCoreBlockEntity core, State state, float partialTick, Vec3 cameraPos,
			ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(core, state, crumblingOverlay);
		Level level = core.getLevel();
		state.renderBeam = false;
		if (level == null) {
			return;
		}

		BlockState blockState = core.getBlockState();
		if (!(blockState.getBlock() instanceof BeetTempleCoreBlock block)
				|| blockState.getValue(BeetTempleCoreBlock.GLOW) < BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
			return;
		}

		state.renderBeam = true;
		state.animationTime = level.getGameTime() + partialTick;
		state.color = block.tier() == BeetTempleTier.SUPREME ? SUPREME_BEAM_COLOR : BEET_BEAM_COLOR;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
		if (!state.renderBeam) {
			return;
		}
		BeaconRenderer.submitBeaconBeam(poseStack, collector, BeaconRenderer.BEAM_LOCATION,
				1.0F, state.animationTime, 0, BEAM_HEIGHT, state.color, 0.2F, 0.25F);
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

	@Override
	public boolean shouldRender(BeetTempleCoreBlockEntity core, Vec3 cameraPos) {
		return Vec3.atCenterOf(core.getBlockPos()).multiply(1.0D, 0.0D, 1.0D)
				.closerThan(cameraPos.multiply(1.0D, 0.0D, 1.0D), getViewDistance());
	}

	public static class State extends BlockEntityRenderState {
		private boolean renderBeam;
		private float animationTime;
		private int color;
	}
}
