package net.gs60419.beetpunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BeetSprinklerBlockEntity extends BlockEntity {
	private static final int MAX_WATER_CHARGES = 16;
	private static final int SPRAY_INTERVAL_TICKS = 200;
	private static final float GROWTH_CHANCE = 0.08F;
	private static final float GROWTH_GLYPH_CHANCE = 0.20F;
	private static final float LV4_WATER_SAVE_CHANCE = 0.50F;

	private int waterCharges;
	private int ticksUntilSpray = SPRAY_INTERVAL_TICKS;

	public BeetSprinklerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BEET_SPRINKLER, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, BeetSprinklerBlockEntity blockEntity) {
		if (level.isClientSide() || blockEntity.waterCharges <= 0) {
			return;
		}

		blockEntity.ticksUntilSpray--;
		if (blockEntity.ticksUntilSpray > 0) {
			return;
		}

		blockEntity.ticksUntilSpray = SPRAY_INTERVAL_TICKS;
		ServerLevel serverLevel = (ServerLevel) level;
		if (blockEntity.spray(serverLevel)) {
			if (!blockEntity.shouldSaveWater(serverLevel)) {
				blockEntity.waterCharges--;
			}
			blockEntity.setChanged();
		}
	}

	public boolean addWater() {
		if (waterCharges >= MAX_WATER_CHARGES) {
			return false;
		}

		waterCharges++;
		setChanged();
		return true;
	}

	public int getWaterCharges() {
		return waterCharges;
	}

	public int currentRadius() {
		if (!(level instanceof ServerLevel serverLevel)) {
			return 1;
		}

		return 1 + nearbyWaterTempleLevel(serverLevel);
	}

	private int nearbyWaterTempleLevel(ServerLevel level) {
		return TempleEffects.nearbyTempleLevel(level, worldPosition, ModBlocks.WATER_TEMPLE_CORE);
	}

	private boolean shouldSaveWater(ServerLevel level) {
		return nearbyWaterTempleLevel(level) >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL
				&& level.getRandom().nextFloat() < LV4_WATER_SAVE_CHANCE;
	}

	private boolean spray(ServerLevel level) {
		boolean affected = false;
		RandomSource random = level.getRandom();
		int radius = currentRadius();
		ServerPlayer nearestPlayer = nearestPlayer(level);

		for (BlockPos target : BlockPos.betweenClosed(worldPosition.offset(-radius, -1, -radius), worldPosition.offset(radius, 1, radius))) {
			BlockState targetState = level.getBlockState(target);
			if ((targetState.is(ModBlocks.BEET_FARMLAND) || targetState.is(ModBlocks.FERTILIZED_BEET_FARMLAND)) && targetState.getValue(FarmlandBlock.MOISTURE) < 7) {
				level.setBlock(target, targetState.setValue(FarmlandBlock.MOISTURE, 7), 3);
				affected = true;
			}

			if (targetState.is(Blocks.BEETROOTS)) {
				boolean grew = growBeetroot(level, target, targetState, random);
				if (grew) {
					tryDropGrowthGlyph(level, target, nearestPlayer);
				}
				affected = grew || affected;
			}
		}

		return affected;
	}

	private ServerPlayer nearestPlayer(ServerLevel level) {
		Player player = level.getNearestPlayer(
				worldPosition.getX() + 0.5,
				worldPosition.getY() + 0.5,
				worldPosition.getZ() + 0.5,
				16.0,
				false);
		return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
	}

	private static void tryDropGrowthGlyph(ServerLevel level, BlockPos pos, ServerPlayer player) {
		if (player == null || !ModAdvancements.isDone(player, BeetTempleTier.WATER.sealTempleAdvancementId())) {
			return;
		}
		if (level.getRandom().nextFloat() < GROWTH_GLYPH_CHANCE) {
			Block.popResource(level, pos, new ItemStack(ModItems.GROWTH_GLYPH));
		}
	}

	private static boolean growBeetroot(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
		int age = state.getValue(BeetrootBlock.AGE);
		if (age >= BeetrootBlock.MAX_AGE || random.nextFloat() >= GROWTH_CHANCE) {
			return false;
		}

		level.setBlock(pos, state.setValue(BeetrootBlock.AGE, age + 1), 3);
		return true;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("WaterCharges", waterCharges);
		output.putInt("TicksUntilSpray", ticksUntilSpray);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		waterCharges = input.getIntOr("WaterCharges", 0);
		ticksUntilSpray = input.getIntOr("TicksUntilSpray", SPRAY_INTERVAL_TICKS);
	}
}
