package net.gs60419.beetpunk;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;

public class BeetRaft extends Boat {
	public BeetRaft(EntityType<? extends Boat> entityType, Level level) {
		super(entityType, level, () -> ModItems.BEET_RAFT);
	}
}
