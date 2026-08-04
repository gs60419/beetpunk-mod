package net.gs60419.beetpunk;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 強化甜菜乾糧 — same appearance as beet ration but with enchantment glint. */
public class EnrichedBeetRationItem extends Item {

    public EnrichedBeetRationItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        }
        return BeetFoodEffects.finishUsing(result, level, entity);
    }
}
