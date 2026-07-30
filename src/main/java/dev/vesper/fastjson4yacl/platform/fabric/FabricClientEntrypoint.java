package dev.vesper.fastjson4yacl.platform.fabric;

//? fabric {

import dev.vesper.fastjson4yacl.FastJson4YACL;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FastJson4YACL.onInitializeClient();
	}

}
//?}
