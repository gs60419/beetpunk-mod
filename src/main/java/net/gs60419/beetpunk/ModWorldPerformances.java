package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class ModWorldPerformances {
	private ModWorldPerformances() {
	}

	public static void templeLevelUp(ServerLevel level, BlockPos pos, int templeLevel) {
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1.05D;
		double z = pos.getZ() + 0.5D;
		int clampedLevel = Math.max(1, Math.min(templeLevel, BeetTempleCoreBlock.MAX_TEMPLE_LEVEL));

		level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 16 + clampedLevel * 7, 0.6D, 0.3D + clampedLevel * 0.08D, 0.6D, 0.08D);
		level.sendParticles(ParticleTypes.END_ROD, x, y + 0.15D, z, 5 + clampedLevel * 3, 0.35D, 0.22D, 0.35D, 0.025D);
		level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.5F + clampedLevel * 0.12F, 0.78F + clampedLevel * 0.13F);

		if (clampedLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
			level.sendParticles(ParticleTypes.FIREWORK, x, y + 0.45D, z, 18, 0.45D, 0.55D, 0.45D, 0.08D);
			level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.25F);
		}
	}

	public static void templeSeal(ServerLevel level, BlockPos pos, int sealCount, boolean completedAllSeals) {
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1.15D;
		double z = pos.getZ() + 0.5D;

		level.sendParticles(ParticleTypes.WITCH, x, y, z, 10, 0.35D, 0.22D, 0.35D, 0.02D);
		level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y + 0.2D, z, 5, 0.3D, 0.25D, 0.3D, 0.02D);
		level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.75F, 0.9F + Math.min(sealCount, 13) * 0.025F);

		if (completedAllSeals) {
			completePilgrimage(level, pos);
		}
	}

	public static void revelation(ServerLevel level, BlockPos pos) {
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1.0D;
		double z = pos.getZ() + 0.5D;

		level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, x, y + 0.25D, z, 30, 0.5D, 0.65D, 0.5D, 0.08D);
		level.sendParticles(ParticleTypes.END_ROD, x, y + 0.35D, z, 12, 0.35D, 0.4D, 0.35D, 0.03D);
		level.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 0.8F, 1.15F);
	}

	private static void completePilgrimage(ServerLevel level, BlockPos pos) {
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 1.45D;
		double z = pos.getZ() + 0.5D;

		level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 48, 0.75D, 0.9D, 0.75D, 0.12D);
		level.sendParticles(ParticleTypes.FIREWORK, x, y + 0.35D, z, 36, 0.7D, 0.7D, 0.7D, 0.1D);
		level.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.BLOCKS, 1.0F, 1.1F);
	}
}
