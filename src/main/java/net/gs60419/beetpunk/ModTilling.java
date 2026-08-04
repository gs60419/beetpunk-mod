package net.gs60419.beetpunk;

public final class ModTilling {
	private ModTilling() {
	}

	public static void register() {
		Beetpunk.LOGGER.info("Registering Beetpunk tilling rules.");
		BeetHoeTillingAccess.registerBeetSoil();
	}
}
