package net.gs60419.beetpunk;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BeetTorchBlock extends TorchBlock {
	public BeetTorchBlock(SimpleParticleType flameParticle, BlockBehaviour.Properties properties) {
		super(flameParticle, properties);
	}
}
