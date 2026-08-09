package net.gs60419.beetpunk;

import java.util.HashSet;
import java.util.Set;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public final class ModTempleAmbience {
	private static final int CHECK_INTERVAL_TICKS = 80;
	private static final int CORE_SEARCH_RADIUS = 64;
	private static int ticks;

	private ModTempleAmbience() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ticks++;
			if (ticks < CHECK_INTERVAL_TICKS) {
				return;
			}
			ticks = 0;

			for (ServerLevel level : server.getAllLevels()) {
				for (BlockPos corePos : findNearbyActiveCores(level)) {
					applyAmbience(level, corePos);
				}
			}
		});
		Beetpunk.LOGGER.info("Registering Beetpunk temple ambience.");
	}

	private static Set<BlockPos> findNearbyActiveCores(ServerLevel level) {
		Set<BlockPos> cores = new HashSet<>();
		level.players().forEach(player -> {
			BlockPos origin = player.blockPosition();
			for (BlockPos target : BlockPos.betweenClosed(
					origin.offset(-CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS),
					origin.offset(CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS))) {
				BlockState state = level.getBlockState(target);
				if (state.getBlock() instanceof BeetTempleCoreBlock && BeetTempleCoreBlock.templeLevel(level, target) > 0) {
					cores.add(target.immutable());
				}
			}
		});
		return cores;
	}

	private static void applyAmbience(ServerLevel level, BlockPos corePos) {
		BlockState state = level.getBlockState(corePos);
		if (!(state.getBlock() instanceof BeetTempleCoreBlock coreBlock)) {
			return;
		}

		int templeLevel = BeetTempleCoreBlock.templeLevel(level, corePos);
		if (templeLevel <= 0) {
			return;
		}

		RandomSource random = level.getRandom();
		DustParticleOptions dust = new DustParticleOptions(themeColor(coreBlock.tier()), 0.75F + templeLevel * 0.08F);
		double x = corePos.getX() + 0.5D;
		double y = corePos.getY() + 0.78D;
		double z = corePos.getZ() + 0.5D;

		level.sendParticles(dust, x, y, z, 1 + templeLevel, 0.42D, 0.18D, 0.42D, 0.01D);

		if (templeLevel >= 2) {
			level.sendParticles(ParticleTypes.END_ROD, x, y + 0.28D, z, 1, 0.22D, 0.12D, 0.22D, 0.01D);
		}
		if (templeLevel >= 3) {
			int range = Math.min(TempleEffects.templeRange(templeLevel), 32);
			int count = templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL ? 3 : 1;
			for (int i = 0; i < count; i++) {
				spawnFieldParticle(level, corePos, range, dust, random);
			}
		}
		if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL && random.nextInt(8) == 0) {
			level.playSound(null, corePos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.22F, 1.4F + random.nextFloat() * 0.25F);
		}
	}

	private static void spawnFieldParticle(ServerLevel level, BlockPos corePos, int range, DustParticleOptions dust, RandomSource random) {
		int xOffset = random.nextInt(range * 2 + 1) - range;
		int zOffset = random.nextInt(range * 2 + 1) - range;
		int yOffset = random.nextInt(9) - 4;
		BlockPos pos = corePos.offset(xOffset, yOffset, zOffset);
		if (level.getBlockState(pos).isAir()) {
			return;
		}
		BlockPos particlePos = pos.above();
		if (!level.getBlockState(particlePos).isAir()) {
			return;
		}

		level.sendParticles(dust,
				particlePos.getX() + 0.5D,
				particlePos.getY() + 0.08D,
				particlePos.getZ() + 0.5D,
				1, 0.18D, 0.04D, 0.18D, 0.0D);
	}

	private static int themeColor(BeetTempleTier tier) {
		return switch (tier) {
			case SEED -> 0x7be05a;
			case SOIL -> 0xb98255;
			case SPROUT -> 0x57d978;
			case WATER -> 0x68c8ff;
			case GROWTH -> 0xff6f7f;
			case LIGHT -> 0xffe45c;
			case LEAF -> 0x69d348;
			case ROOT -> 0xc25b86;
			case BEET -> 0xe03a78;
			case SOUP -> 0xff9b4a;
			case DYE -> 0xff2f89;
			case PIG -> 0xff8aae;
			case SUPREME -> 0xc26bff;
		};
	}
}
