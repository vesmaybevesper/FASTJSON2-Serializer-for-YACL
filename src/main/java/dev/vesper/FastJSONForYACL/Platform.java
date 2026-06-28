package dev.vesper.FastJSONForYACL;

//? fabric {
import dev.vesper.FastJSONForYACL.fabric.FabricPlatformImpl;
//?}
//? neoforge {
//?}

public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new dev.vesper.AIUTD.neoforge.NeoforgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);
    String loader();

}
