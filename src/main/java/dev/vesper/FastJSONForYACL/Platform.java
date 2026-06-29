package dev.vesper.FastJSONForYACL;

//? fabric {
import dev.vesper.FastJSONForYACL.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import dev.vesper.FastJSONForYACL.neoforge.NeoforgePlatformImpl;
*///?}

public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new NeoforgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);
    String loader();

}
