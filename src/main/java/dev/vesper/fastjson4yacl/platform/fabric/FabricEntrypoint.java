package dev.vesper.fastjson4yacl.platform.fabric;

//? fabric {

import dev.vesper.fastjson4yacl.ModTemplate;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		ModTemplate.onInitialize();
	}
}
//?}
