package net.gs60419.beetpunk;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BeetWaterItem extends Item {
	private static final float WATER_GLYPH_CHANCE = 0.20f;

	public BeetWaterItem(Properties properties) {
		super(properties);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		super.onCraftedBy(stack, player);
		Level level = player.level();
		if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;
		if (!ModAdvancements.isDone(serverPlayer, BeetTempleTier.SPROUT.sealTempleAdvancementId())) return;
		if (level.getRandom().nextFloat() < WATER_GLYPH_CHANCE) {
			Block.popResource(level, player.blockPosition(), new ItemStack(ModItems.WATER_GLYPH));
		}
	}
}
