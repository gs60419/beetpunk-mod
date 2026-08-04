package net.gs60419.beetpunk;

import java.util.EnumMap;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class ModArmorMaterials {
	private static final TagKey<Item> BEET_IRON_REPAIR = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet_iron_armor_repair"));

	public static final ArmorMaterial BEET_IRON = new ArmorMaterial(
		15,
		ironDefense(),
		9,
		SoundEvents.ARMOR_EQUIP_IRON,
		0.0F,
		0.0F,
		BEET_IRON_REPAIR,
		EquipmentAssets.IRON
	);

	private ModArmorMaterials() {
	}

	private static EnumMap<ArmorType, Integer> ironDefense() {
		EnumMap<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
		defense.put(ArmorType.BOOTS, 2);
		defense.put(ArmorType.LEGGINGS, 5);
		defense.put(ArmorType.CHESTPLATE, 6);
		defense.put(ArmorType.HELMET, 2);
		defense.put(ArmorType.BODY, 5);
		return defense;
	}
}
