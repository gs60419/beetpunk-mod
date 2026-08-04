package net.gs60419.beetpunk;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.Boat;

public final class ModEntityTypes {
	public static final EntityType<BeetRaft> BEET_RAFT = Registry.register(
		BuiltInRegistries.ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_raft"),
		EntityType.Builder.<BeetRaft>of(BeetRaft::new, MobCategory.MISC)
			.sized(1.375F, 0.5625F)
			.clientTrackingRange(10)
			.build(entityKey("beet_raft"))
	);
	public static final EntityType<BeetChestRaft> BEET_CHEST_RAFT = Registry.register(
		BuiltInRegistries.ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_chest_raft"),
		EntityType.Builder.<BeetChestRaft>of(BeetChestRaft::new, MobCategory.MISC)
			.sized(1.375F, 0.5625F)
			.clientTrackingRange(10)
			.build(entityKey("beet_chest_raft"))
	);

	private ModEntityTypes() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk entities.");
	}

	public static AbstractBoat createRaft(boolean chest, net.minecraft.world.level.Level level) {
		return chest ? new BeetChestRaft(BEET_CHEST_RAFT, level) : new BeetRaft(BEET_RAFT, level);
	}

	private static ResourceKey<EntityType<?>> entityKey(String path) {
		return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, path));
	}
}
