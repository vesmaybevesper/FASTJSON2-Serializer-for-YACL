package dev.vesper.FastJSONForYACL.common.serializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.api.SerialField;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class FastJsonConfigSerializer<T> extends ConfigSerializer<T> {
    private final Path path;
    private final JSONWriter.Feature[] writerFeatures;
    private final JSONReader.Feature[] readerFeatures;
    private final Map<Type, ObjectWriter<?>> typeWriters;
    private final Map<Type, ObjectReader<?>> typeReaders;


    public FastJsonConfigSerializer(ConfigClassHandler<T> config, Path path, JSONWriter.Feature[] writerFeatures, JSONReader.Feature[] readerFeatures, Map<Type, ObjectWriter<?>> typeWriters, Map<Type, ObjectReader<?>> typeReaders) {
        super(config);
        this.path = path;
        this.writerFeatures = writerFeatures;
        this.readerFeatures = readerFeatures;
        this.typeWriters = typeWriters;
        this.typeReaders = typeReaders;
    }

    @Override
    public void save() {
        YACLConstants.LOGGER.info("Serializing {} to '{}'", this.config.getClass(), this.path);

        try {
            JSONObject root = new JSONObject();

            for (ConfigField<?> field : this.config.fields()){
                SerialField serial = field.serial().orElse(null);
                if (serial == null) continue;
                Object value;

                try {
                    value = field.access().get();
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to read config field '{}'. Serializing as null.", serial.serialName(), e);
                    root.put(serial.serialName(), null);
                    continue;
                }

                @SuppressWarnings("unchecked")
                ObjectWriter<Object> writer = (ObjectWriter<Object>) typeWriters.get(field.access().type());
                if (writer != null) {
                    try {
                        String fragment = JSON.toJSONString(value, writerFeatures);
                        root.put(serial.serialName(), JSON.parse(fragment));
                    } catch (Exception e) {
                        YACLConstants.LOGGER.error("Failed to serialize config field '{}' with custom writer. Serializing as null.", serial.serialName(), e);
                        root.put(serial.serialName(), null);
                    }
                } else {
                    try {
                        root.put(serial.serialName(), value);
                    } catch (Exception e) {
                        YACLConstants.LOGGER.error("Failed to serialize config field '{}'. Serializing as null.", serial.serialName(), e);
                        root.put(serial.serialName(), null);
                    }
                }
            }

            String json = root.toString(writerFeatures);
            Files.createDirectories(this.path.getParent());
            Files.writeString(this.path, json, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            YACLConstants.LOGGER.error("Failed to serialize config class '{}'.", this.config.configClass().getSimpleName(), e);
        }
    }

    @Override
    public LoadResult loadSafely(Map<ConfigField<?>, FieldAccess<?>> bufferAccessMap) {
        if (!Files.exists(this.path, new LinkOption[0])) {
            YACLConstants.LOGGER.info("Config file '{}' does not exist. Creating it with default values.", this.path);
            this.save();
            return LoadResult.NO_CHANGE;
        }

        YACLConstants.LOGGER.info("Deserializing {} from '{}'", this.config.configClass().getSimpleName(), this.path);

        Map<String, ConfigField<?>> fieldMap = Arrays.stream(this.config.fields()).filter(f -> f.serial().isPresent()).collect(Collectors.toMap(f -> f.serial().orElseThrow().serialName(), Function.identity()));
        Set<String> missingFields = fieldMap.keySet();
        boolean dirty = false;

        String rawJson;
        try {
            rawJson = Files.readString(this.path);
        } catch (IOException e) {
            YACLConstants.LOGGER.error("Failed to read config file '{}'.", this.path, e);
            return LoadResult.FAILURE;
        }

        JSONObject root;

        try{
            root = JSON.parseObject(rawJson, readerFeatures);
        } catch (Exception e) {
            YACLConstants.LOGGER.error("Failed to parse config file '{}'.", this.path, e);
            return LoadResult.FAILURE;
        }

        for (String key : root.keySet()){
            ConfigField<?> field = fieldMap.get(key);
            missingFields.remove(key);

            if (field == null) {
                YACLConstants.LOGGER.warn("Found unknown config field '{}'.", key);
                continue;
            }

            @SuppressWarnings("unchecked")
            FieldAccess<Object> bufferAccess = (FieldAccess<Object>) bufferAccessMap.get(field);
            SerialField serial = field.serial().orElse(null);
            if (serial == null) continue;

            Object rawValue = root.get(key);

            if (rawValue == null && !serial.nullable()) {
                YACLConstants.LOGGER.warn("Found null value in non-nullable config field '{}'. Leaving field as default and marking as dirty.", key);
                dirty = true;
                continue;
            }
            @SuppressWarnings("unchecked")
            ObjectReader<Object> reader = (ObjectReader<Object>) typeReaders.get(bufferAccess.type());
            try {
                if (reader != null) {
                    String fragment = JSON.toJSONString(rawValue);
                    Object parsed = JSON.parseObject(fragment, bufferAccess.type(), readerFeatures);
                    bufferAccess.set(parsed);
                } else  {
                    Object parsed = root.getObject(key, bufferAccess.type());
                    bufferAccess.set(parsed);
                }
            } catch (Exception e) {
                YACLConstants.LOGGER.error(
                        "Failed to deserialize config field '{}'. Leaving as default.", key, e);
            }
        }

        if (!missingFields.isEmpty()) {
            for (String missingField : missingFields) {
                if (fieldMap.get(missingField).serial().orElseThrow().required()){
                    dirty = true;
                    YACLConstants.LOGGER.warn("Missing required config field '{}'. Re-saving as default.", missingField);
                }
            }
        }

        return dirty ? LoadResult.DIRTY : LoadResult.SUCCESS;
    }

    @Override
    @Deprecated
    public void load() {
        YACLConstants.LOGGER.warn("Calling ConfigSerializer#load() directly is deprecated. Please use ConfigClassHandler#load() instead.");
        this.config.load();
    }

    // I really hate that these passes through both Mojangs json codec and GSON before it gets to FastJSON, something to fix up later
    public static class StyleWriter implements ObjectWriter<Style> {
        @Override
        public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
            if (!(object instanceof Style style)) {
                jsonWriter.writeNull();
                return;
            }
            String encoded = Style.Serializer.CODEC
                    .encodeStart(JsonOps.INSTANCE, style)
                    .result()
                    .map(JsonElement::toString)
                    .orElse("null");
            jsonWriter.writeRaw(encoded);
        }
    }

    public static class StyleReader implements ObjectReader<Style> {
        @Override
        public Style readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
            JSONObject obj = jsonReader.readJSONObject();
            if (obj == null) return Style.EMPTY;
            com.google.gson.JsonElement element =
                    com.google.gson.JsonParser.parseString(obj.toJSONString());
            return Style.Serializer.CODEC
                    .parse(JsonOps.INSTANCE, element)
                    .result()
                    .orElse(Style.EMPTY);
        }
    }

    public static class ColorWriter implements ObjectWriter<Color> {
        @Override
        public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
            if (!(object instanceof Color color)) {
                jsonWriter.writeNull();
                return;
            }
            jsonWriter.writeInt32(color.getRGB());
        }
    }

    public static class ColorReader implements ObjectReader<Color> {
        @Override
        public Color readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
            return new Color(jsonReader.readInt32(), true);
        }
    }

    public static class ItemWriter implements ObjectWriter<Item> {
        @Override
        public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
            if (!(object instanceof Item item)) {
                jsonWriter.writeNull();
                return;
            }
            jsonWriter.writeString(BuiltInRegistries.ITEM.getKey(item).toString());
        }
    }

    public static class ItemReader implements ObjectReader<Item> {
        @Override
        public Item readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
            return ItemRegistryHelper.getItemFromName(jsonReader.readString());
        }
    }


    @ApiStatus.Internal
    public static class Builder<T> implements FastJsonConfigSerializerBuilder<T> {

        private final ConfigClassHandler<T> config;
        private Path path;

        private JSONWriter.Feature[] writerFeatures = {
                JSONWriter.Feature.PrettyFormat,
                JSONWriter.Feature.WriteNulls,
                JSONWriter.Feature.FieldBased
        };

        private  JSONReader.Feature[] readerFeatures = {
                JSONReader.Feature.FieldBased,
                JSONReader.Feature.SupportAutoType
        };

        private final Map<Type, ObjectWriter<?>> typeWriters = new HashMap<>();
        private final Map<Type, ObjectReader<?>> typeReaders = new HashMap<>();

        public  Builder(ConfigClassHandler<T> config) {
            this.config = config;
            registerDefaultAdapters();
        }

        private  void registerDefaultAdapters() {
            typeWriters.put(Style.class, new StyleWriter());
            typeReaders.put(Style.class, new StyleReader());

            typeWriters.put(Color.class, new ColorWriter());
            typeReaders.put(Color.class, new ColorReader());

            typeWriters.put(Item.class, new ItemWriter());
            typeReaders.put(Item.class, new ItemReader());

        }

        @Override
        public FastJsonConfigSerializerBuilder<T> setPath(Path path) {
            this.path = path;
            return this;
        }

        @Override
        public FastJsonConfigSerializerBuilder<T> overrideWriterFeatures(JSONWriter.Feature... features) {
            this.writerFeatures = features;
            return this;
        }

        @Override
        public FastJsonConfigSerializerBuilder<T> appendWriterFeatures(UnaryOperator<JSONWriter.Feature[]> operator) {
            this.writerFeatures = operator.apply(this.writerFeatures);
            return this;
        }

        @Override
        public FastJsonConfigSerializerBuilder<T> overrideReaderFeatures(JSONReader.Feature... features) {
            this.readerFeatures = features;
            return this;
        }

        @Override
        public FastJsonConfigSerializerBuilder<T> appendReaderFeatures(UnaryOperator<JSONReader.Feature[]> operator) {
            this.readerFeatures = operator.apply(this.readerFeatures);
            return this;
        }

        @Override
        public <V> FastJsonConfigSerializerBuilder<T> registerTypeWriter(Type type, ObjectWriter<V> writer) {
            typeWriters.put(type, writer);
            return this;
        }

        @Override
        public <V> FastJsonConfigSerializerBuilder<T> registerTypeReader(Type type, ObjectReader<V> reader) {
            typeReaders.put(type, reader);
            return this;
        }

        @Override
        public ConfigSerializer<T> build() {
            return new FastJsonConfigSerializer<>(config, path, writerFeatures, readerFeatures, Map.copyOf(typeWriters), Map.copyOf(typeReaders));
        }
    }
}