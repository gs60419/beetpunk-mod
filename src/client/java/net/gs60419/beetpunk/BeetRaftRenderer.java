package net.gs60419.beetpunk;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.Identifier;

public class BeetRaftRenderer extends AbstractBoatRenderer {
	private final EntityModel<BoatRenderState> model;

	public BeetRaftRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, boolean chest) {
		super(context, Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID,
				chest ? "textures/entity/chest_boat/beet_raft.png" : "textures/entity/boat/beet_raft.png"));
		this.model = new BoatModel(context.bakeLayer(layer));
	}

	@Override
	protected EntityModel<BoatRenderState> model() {
		return model;
	}
}
