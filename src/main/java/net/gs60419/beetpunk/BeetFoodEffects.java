package net.gs60419.beetpunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class BeetFoodEffects {
	private BeetFoodEffects() {
	}

	public static ItemStack finishUsing(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof Player player) {
			applySoupTempleEffects(serverLevel, player);
		}
		return stack;
	}

	private static void applySoupTempleEffects(ServerLevel level, Player player) {
		int templeLevel = TempleEffects.nearbyTempleLevel(level, player.blockPosition(), ModBlocks.SOUP_TEMPLE_CORE);
		if (templeLevel <= 0) {
			return;
		}

		player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80 + templeLevel * 40, templeLevel >= 3 ? 1 : 0));
		if (templeLevel >= 2) {
			player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1, 0));
		}
		if (templeLevel >= 3) {
			player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 240, 0));
		}
		if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
			player.removeEffect(MobEffects.HUNGER);
			player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 120, 0));
		}
	}
}
