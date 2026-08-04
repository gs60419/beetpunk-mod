package net.gs60419.beetpunk;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * 甜菜纖維 — crafting this has a chance to drop 茂葉聖文字 (LEAF glyph)
 * once the player has activated the 耀光神殿 (LIGHT temple core).
 */
public class BeetFiberItem extends Item {

    private static final float LEAF_GLYPH_CHANCE = 0.15f;

    public BeetFiberItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        super.onCraftedBy(stack, player);
        Level level = player.level();
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;
        if (!ModAdvancements.isDone(serverPlayer, BeetTempleTier.LIGHT.sealTempleAdvancementId())) return;
        if (level.getRandom().nextFloat() < LEAF_GLYPH_CHANCE) {
            Block.popResource(level, player.blockPosition(), new ItemStack(ModItems.LEAF_GLYPH));
        }
    }
}
