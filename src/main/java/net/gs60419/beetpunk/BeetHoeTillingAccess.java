package net.gs60419.beetpunk;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public final class BeetHoeTillingAccess extends HoeItem {
	private BeetHoeTillingAccess() {
		super(ToolMaterial.WOOD, 0.0F, -3.0F, new Item.Properties());
	}

	public static void registerBeetSoil() {
		TILLABLES.put(ModBlocks.BEET_SOIL, Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(ModBlocks.BEET_FARMLAND.defaultBlockState())));
		TILLABLES.put(ModBlocks.FERTILIZED_BEET_SOIL, Pair.of(HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(ModBlocks.FERTILIZED_BEET_FARMLAND.defaultBlockState())));
	}
}
