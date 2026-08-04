package net.gs60419.beetpunk;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BeetWallTorchBlock extends WallTorchBlock {
	public BeetWallTorchBlock(SimpleParticleType flameParticle, BlockBehaviour.Properties properties) {
		super(flameParticle, properties);
	}
}
