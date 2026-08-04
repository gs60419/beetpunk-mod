package net.gs60419.beetpunk;

import java.lang.reflect.Method;

import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class ModWoodTypes {
	public static final BlockSetType BEET_SET = registerBlockSetType(
		new BlockSetType(
			"beet",
			true,
			true,
			true,
			BlockSetType.PressurePlateSensitivity.EVERYTHING,
			SoundType.BAMBOO_WOOD,
			SoundEvents.BAMBOO_WOOD_DOOR_CLOSE,
			SoundEvents.BAMBOO_WOOD_DOOR_OPEN,
			SoundEvents.BAMBOO_WOOD_TRAPDOOR_CLOSE,
			SoundEvents.BAMBOO_WOOD_TRAPDOOR_OPEN,
			SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF,
			SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_OFF,
			SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON
		)
	);
	public static final WoodType BEET = registerWoodType(
		Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "beet")
	);

	private ModWoodTypes() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk wood type.");
	}

	private static BlockSetType registerBlockSetType(BlockSetType type) {
		try {
			Method register = findPrivateRegister(BlockSetType.class, BlockSetType.class);
			register.setAccessible(true);
			return (BlockSetType) register.invoke(null, type);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Could not register Beetpunk block set type.", exception);
		}
	}

	private static WoodType registerWoodType(Identifier id) {
		return new WoodTypeBuilder()
				.soundType(SoundType.BAMBOO_WOOD)
				.hangingSignSoundType(SoundType.BAMBOO_WOOD_HANGING_SIGN)
				.fenceGateCloseSound(SoundEvents.BAMBOO_WOOD_FENCE_GATE_CLOSE)
				.fenceGateOpenSound(SoundEvents.BAMBOO_WOOD_FENCE_GATE_OPEN)
				.register(id, BEET_SET);
	}

	private static Method findPrivateRegister(Class<?> owner, Class<?> type) throws NoSuchMethodException {
		for (Method method : owner.getDeclaredMethods()) {
			if (method.getReturnType() == type && method.getParameterCount() == 1 && method.getParameterTypes()[0] == type) {
				return method;
			}
		}
		throw new NoSuchMethodException(owner.getName() + " register(" + type.getName() + ")");
	}
}
