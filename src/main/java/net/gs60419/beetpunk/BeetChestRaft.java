package net.gs60419.beetpunk;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.level.Level;

public class BeetChestRaft extends ChestBoat {
	public BeetChestRaft(EntityType<? extends ChestBoat> entityType, Level level) {
		super(entityType, level, () -> ModItems.BEET_CHEST_RAFT);
	}
}
