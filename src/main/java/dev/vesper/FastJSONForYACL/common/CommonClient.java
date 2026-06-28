package dev.vesper.FastJSONForYACL.common;

import dev.vesper.FastJSONForYACL.config.Config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static dev.vesper.FastJSONForYACL.config.Config.localVersion;

public class CommonClient {
    public static void init(){
        try {
            if (!UpdateChecker.hasChecked){
                if (Config.versionAPI.isEmpty()) {
                    UpdateChecker.needUpdate = false;
                    return;
                }
                UpdateChecker.needUpdate = !Objects.equals(localVersion, UpdateChecker.getVersionNumber());
                UpdateChecker.hasChecked = true;
            }
        } catch (URISyntaxException | IOException ignored) {}
        Util.setColors();
    }
}
