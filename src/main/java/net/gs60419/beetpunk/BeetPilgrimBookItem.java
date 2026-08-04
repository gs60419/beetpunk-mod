package net.gs60419.beetpunk;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BeetPilgrimBookItem extends Item {
	public BeetPilgrimBookItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
			ServerPlayNetworking.send(serverPlayer, new BeetPilgrimBookPayload(mask(serverPlayer, true), mask(serverPlayer, false)));
		}
		return InteractionResult.SUCCESS;
	}

	private static int mask(ServerPlayer player, boolean glyphPage) {
		BeetTempleTier[] tiers = BeetTempleTier.values();
		int mask = 0;
		for (int index = 0; index < tiers.length; index++) {
			BeetTempleTier tier = tiers[index];
			if (ModAdvancements.isDone(player, glyphPage ? tier.findGlyphAdvancementId() : tier.sealTempleAdvancementId())) {
				mask |= 1 << index;
			}
		}
		return mask;
	}
}
