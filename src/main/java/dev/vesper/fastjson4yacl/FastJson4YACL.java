package dev.vesper.fastjson4yacl;

import dev.vesper.fastjson4yacl.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import dev.vesper.fastjson4yacl.platform.fabric.FabricPlatform;
//?} neoforge {
/*import dev.vesper.fastjson4yacl.platform.neoforge.NeoforgePlatform;
 *///?} forge {
/*import dev.vesper.fastjson4yacl.platform.forge.ForgePlatform;
 *///?}

@SuppressWarnings("LoggingSimilarMessage")
public class FastJson4YACL {

	public static final String MOD_ID = /*$ mod_id*/ "fastjson4yacl";
	public static final String MOD_VERSION = /*$ mod_version*/ "1.0.7";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "FASTJSON2 Serializer for YACL";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, FastJson4YACL.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, FastJson4YACL.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		 *///?}
	}
}
