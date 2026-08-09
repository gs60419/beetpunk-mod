package net.gs60419.beetpunk.mixin;

import net.gs60419.beetpunk.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
	@Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
	private void beetpunk$allowBeetFarmland(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(ModBlocks.BEET_FARMLAND) || state.is(ModBlocks.FERTILIZED_BEET_FARMLAND)) {
			cir.setReturnValue(true);
		}
	}
}
