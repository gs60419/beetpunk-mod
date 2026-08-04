package net.gs60419.beetpunk;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BeetPrayerBarrelRenderer implements BlockEntityRenderer<BeetProcessingTableBlockEntity, BeetPrayerBarrelRenderer.State> {
	private static final int FULL_BRIGHT_LIGHT = 0x00F000F0;
	private final BlockModelResolver blockModelResolver;
	private final BlockDisplayContext blockDisplayContext = BlockDisplayContext.create();

	public BeetPrayerBarrelRenderer(BlockEntityRendererProvider.Context context) {
		blockModelResolver = new BlockModelResolver(Minecraft.getInstance().getModelManager());
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(BeetProcessingTableBlockEntity barrel, State state, float partialTick, Vec3 cameraPos,
			ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(barrel, state, crumblingOverlay);
		state.render = false;
		state.angle = 0.0F;
		state.model.clear();

		BlockState blockState = barrel.getBlockState();
		if (!isPrayerBarrel(blockState)) {
			return;
		}

		state.angle = barrel.spinAngle(partialTick);
		Level level = barrel.getLevel();
		BlockPos pos = barrel.getBlockPos();
		if (level != null) {
			BeetCrankBaseBlockEntity base = findBase(level, pos);
			if (base != null) {
				state.angle += base.spinAngle(partialTick);
				state.render = base.isSpinning();
			}
		}
		state.render = state.render || barrel.isCranking();
		if (!state.render) {
			return;
		}
		if (blockState.hasProperty(BeetProcessingTableBlock.SPINNING)) {
			blockState = blockState.setValue(BeetProcessingTableBlock.SPINNING, false);
		}
		blockModelResolver.update(state.model, blockState, blockDisplayContext);
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
		if (!state.render || state.model.isEmpty()) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(state.angle));
		poseStack.translate(-0.5F, -0.5F, -0.5F);
		int light = state.lightCoords == 0 ? FULL_BRIGHT_LIGHT : state.lightCoords;
		state.model.submit(poseStack, collector, light, 0, -1);
		poseStack.popPose();
	}

	private static boolean isPrayerBarrel(BlockState state) {
		return state.is(ModBlocks.BEET_EXTRACTOR_BARREL)
			|| state.is(ModBlocks.BEET_GRINDER_BARREL)
			|| state.is(ModBlocks.BEET_WASHING_BARREL);
	}

	private static BeetCrankBaseBlockEntity findBase(Level level, BlockPos pos) {
		BlockPos scanPos = pos.below();
		for (int i = 0; i < 4; i++) {
			if (level.getBlockEntity(scanPos) instanceof BeetCrankBaseBlockEntity base) {
				return base;
			}
			if (!isPrayerBarrel(level.getBlockState(scanPos))) {
				return null;
			}
			scanPos = scanPos.below();
		}
		return null;
	}

	public static class State extends BlockEntityRenderState {
		private final BlockModelRenderState model = new BlockModelRenderState();
		private boolean render;
		private float angle;
	}
}
