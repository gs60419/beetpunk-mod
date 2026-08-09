package net.gs60419.beetpunk.mixin;

import net.gs60419.beetpunk.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
public abstract class PigMixin {
	@Shadow
	protected GoalSelector goalSelector;

	@Inject(method = "registerGoals", at = @At("TAIL"))
	private void beetpunk$addBeetOnAStickTemptGoal(CallbackInfo ci) {
		Pig pig = (Pig) (Object) this;
		goalSelector.addGoal(4, new TemptGoal(pig, 1.2, stack -> stack.is(ModItems.BEET_ON_A_STICK), false));
	}

	@Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
	private void beetpunk$allowBeetOnAStickControl(CallbackInfoReturnable<LivingEntity> cir) {
		if (cir.getReturnValue() != null) {
			return;
		}

		Pig pig = (Pig) (Object) this;
		if (!pig.isSaddled()) {
			return;
		}

		Entity passenger = pig.getFirstPassenger();
		if (passenger instanceof Player player && player.isHolding(ModItems.BEET_ON_A_STICK)) {
			cir.setReturnValue(player);
		}
	}
}
