package net.gs60419.beetpunk;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BeetCampfireBlockItem extends BlockItem {
	private static final float LIGHT_GLYPH_CHANCE = 0.20F;

	public BeetCampfireBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		super.onCraftedBy(stack, player);
		Level level = player.level();
		if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;
		if (!ModAdvancements.isDone(serverPlayer, BeetTempleTier.GROWTH.sealTempleAdvancementId())) return;
		if (level.getRandom().nextFloat() < LIGHT_GLYPH_CHANCE) {
			Block.popResource(level, player.blockPosition(), new ItemStack(ModItems.LIGHT_GLYPH));
		}
	}
}
