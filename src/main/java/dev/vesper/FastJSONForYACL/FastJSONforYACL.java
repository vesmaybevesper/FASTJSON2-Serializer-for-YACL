package dev.vesper.FastJSONForYACL;

//? fabric {
import net.fabricmc.loader.api.FabricLoader;
//?}
//? neoforge {
/*import net.neoforged.fml.ModList;
*///?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastJSONforYACL {

    public static final String MOD_ID = "fastjson-yacl";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOG.info("Initializing {} on {}", MOD_ID, Platform.INSTANCE.loader());
    }

    public static boolean isModLoaded(String modId){
        //? fabric {
        return FabricLoader.getInstance().isModLoaded(modId);
        //?}
        //? neoforge {
        /*return ModList.get().isLoaded(modId);
        *///?}
    }

}
