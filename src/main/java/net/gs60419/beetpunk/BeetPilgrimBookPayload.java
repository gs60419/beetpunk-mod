package net.gs60419.beetpunk;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BeetPilgrimBookPayload(int glyphMask, int sealMask) implements CustomPacketPayload {
	public static final Type<BeetPilgrimBookPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "open_pilgrim_book"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BeetPilgrimBookPayload> CODEC = StreamCodec.of(
		(buffer, payload) -> {
			buffer.writeVarInt(payload.glyphMask);
			buffer.writeVarInt(payload.sealMask);
		},
		buffer -> new BeetPilgrimBookPayload(buffer.readVarInt(), buffer.readVarInt())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
