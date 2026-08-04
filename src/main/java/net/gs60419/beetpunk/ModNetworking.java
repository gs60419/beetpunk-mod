package net.gs60419.beetpunk;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetworking {
	private ModNetworking() {
	}

	public static void registerCommon() {
		PayloadTypeRegistry.clientboundPlay().register(BeetPilgrimBookPayload.TYPE, BeetPilgrimBookPayload.CODEC);
	}
}
