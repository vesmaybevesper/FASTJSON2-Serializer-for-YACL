package dev.vesper.FastJSONForYACL.fabric;

//? fabric {
import dev.vesper.FastJSONForYACL.FastJSONforYACL;
import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FastJSONforYACL.LOG.info("Initializing {} Client", FastJSONforYACL.MOD_ID);
    }
}
//?}