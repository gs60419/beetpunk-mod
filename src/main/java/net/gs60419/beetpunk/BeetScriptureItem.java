package net.gs60419.beetpunk;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BeetScriptureItem extends Item {
	private final boolean foil;

	public BeetScriptureItem(boolean foil, Properties properties) {
		super(properties);
		this.foil = foil;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return foil || super.isFoil(stack);
	}
}
