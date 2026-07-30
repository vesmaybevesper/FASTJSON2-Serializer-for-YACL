package dev.vesper.fastjson4yacl.platform.fabric;

//? fabric {

import dev.vesper.fastjson4yacl.FastJson4YACL;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		FastJson4YACL.onInitialize();
	}
}
//?}
