package net.gs60419.beetpunk;

import java.util.HashSet;
import java.util.Set;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.phys.AABB;

public final class ModSupremeTempleEffects {
	private static final int CHECK_INTERVAL_TICKS = 200;
	private static final int CORE_SEARCH_RADIUS = 64;
	private static final int MASTER_LEVEL = VillagerData.MAX_VILLAGER_LEVEL;
	private static final int MASTER_XP = VillagerData.getMinXpPerLevel(MASTER_LEVEL);
	private static final int DISCOUNT = -16;
	private static int ticks;

	private ModSupremeTempleEffects() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ticks++;
			if (ticks < CHECK_INTERVAL_TICKS) {
				return;
			}
			ticks = 0;

			for (ServerLevel level : server.getAllLevels()) {
				Set<BlockPos> cores = findNearbySupremeCores(level);
				for (BlockPos corePos : cores) {
					applySupremeField(level, corePos);
				}
			}
		});
		Beetpunk.LOGGER.info("Registering Beetpunk supreme temple effects.");
	}

	private static Set<BlockPos> findNearbySupremeCores(ServerLevel level) {
		Set<BlockPos> cores = new HashSet<>();
		level.players().forEach(player -> {
			BlockPos origin = player.blockPosition();
			for (BlockPos target : BlockPos.betweenClosed(
					origin.offset(-CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS, -CORE_SEARCH_RADIUS),
					origin.offset(CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS, CORE_SEARCH_RADIUS))) {
				if (level.getBlockState(target).is(ModBlocks.SUPREME_TEMPLE_CORE)
						&& BeetTempleCoreBlock.templeLevel(level, target) > 0) {
					cores.add(target.immutable());
				}
			}
		});
		return cores;
	}

	private static void applySupremeField(ServerLevel level, BlockPos corePos) {
		int templeLevel = BeetTempleCoreBlock.templeLevel(level, corePos);
		int range = TempleEffects.templeRange(templeLevel);
		if (range <= 0) {
			return;
		}

		AABB area = new AABB(corePos).inflate(range);
		for (Villager villager : level.getEntitiesOfClass(Villager.class, area, Villager::isAlive)) {
			if (templeLevel >= 2) {
				promoteToMaster(villager);
			}
			if (templeLevel >= 3) {
				villager.restock();
			}
			if (templeLevel >= BeetTempleCoreBlock.MAX_TEMPLE_LEVEL) {
				discountOffers(villager);
			}
		}
	}

	private static void promoteToMaster(Villager villager) {
		if (villager.getVillagerData().profession().is(VillagerProfession.NONE)) {
			return;
		}
		if (villager.getVillagerData().level() < MASTER_LEVEL) {
			villager.setVillagerData(villager.getVillagerData().withLevel(MASTER_LEVEL));
			villager.setVillagerXp(MASTER_XP);
		}
	}

	private static void discountOffers(Villager villager) {
		for (MerchantOffer offer : villager.getOffers()) {
			offer.setSpecialPriceDiff(DISCOUNT);
		}
	}
}
