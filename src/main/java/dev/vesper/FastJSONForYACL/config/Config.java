package dev.vesper.FastJSONForYACL.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;

import static dev.vesper.FastJSONForYACL.FastJSONforYACL.MOD_ID;

public class Config {
    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.fromNamespaceAndPath(MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("modid.json5"))
                    .build())
            .build();

    public static Screen config(Screen parent){
        return HANDLER.generateGui().generateScreen(parent);
    }
}