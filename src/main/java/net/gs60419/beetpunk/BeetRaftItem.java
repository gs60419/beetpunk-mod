package net.gs60419.beetpunk;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BeetRaftItem extends Item {
	private final boolean chest;

	public BeetRaftItem(boolean chest, Properties properties) {
		super(properties);
		this.chest = chest;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (hit.getType() == HitResult.Type.MISS) {
			return InteractionResult.PASS;
		}

		Vec3 view = player.getViewVector(1.0F);
		List<Entity> nearby = level.getEntities(player, player.getBoundingBox().expandTowards(view.scale(5.0D)).inflate(1.0D), entity -> entity.isPickable() && !entity.isSpectator());
		if (!nearby.isEmpty()) {
			return InteractionResult.PASS;
		}

		AbstractBoat raft = ModEntityTypes.createRaft(chest, level);
		Vec3 location = hit.getLocation();
		raft.setPos(location.x, location.y, location.z);
		raft.setYRot(player.getYRot());
		raft.xo = location.x;
		raft.yo = location.y;
		raft.zo = location.z;
		if (!level.noCollision(raft, raft.getBoundingBox().inflate(-0.1D))) {
			return InteractionResult.FAIL;
		}

		if (!level.isClientSide()) {
			level.addFreshEntity(raft);
			level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.ENTITY_PLACE, BlockPos.containing(location));
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResult.SUCCESS;
	}
}
