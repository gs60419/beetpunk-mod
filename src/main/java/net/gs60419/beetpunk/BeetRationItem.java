package net.gs60419.beetpunk;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * 甜菜乾糧 — stackable beet food, better than vanilla beetroot soup.
 * Crafting this item has a chance to drop a 甘湯聖文字 (SOUP glyph)
 * once the player has activated the 甜菜神殿 (BEET temple core).
 */
public class BeetRationItem extends Item {

    private static final float SOUP_GLYPH_CHANCE = 0.15f;

    public BeetRationItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        super.onCraftedBy(stack, player);
        Level level = player.level();
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;
        if (!ModAdvancements.isDone(serverPlayer, BeetTempleTier.BEET.sealTempleAdvancementId())) return;
        if (level.getRandom().nextFloat() < SOUP_GLYPH_CHANCE) {
            Block.popResource(level, player.blockPosition(), new ItemStack(ModItems.SOUP_GLYPH));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
        }
        return BeetFoodEffects.finishUsing(result, level, entity);
    }
}
