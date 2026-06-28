package dev.vesper.FastJSONForYACL.fabric;

//? fabric {
import dev.vesper.FastJSONForYACL.FastJSONforYACL;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        FastJSONforYACL.init();
    }

}
//?}