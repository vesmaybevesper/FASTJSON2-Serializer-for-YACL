package dev.vesper.FastJSONForYACL.common.serializer;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

public interface FastJsonConfigSerializerBuilder<T> {
    static <T> FastJsonConfigSerializerBuilder<T> create(ConfigClassHandler<T> config) {
        return new FastJsonConfigSerializer.Builder<>(config);
    }

    FastJsonConfigSerializerBuilder<T> setPath(Path path);

    FastJsonConfigSerializerBuilder<T> overrideWriterFeatures(JSONWriter.Feature... features);

    FastJsonConfigSerializerBuilder<T> appendWriterFeatures(UnaryOperator<JSONWriter.Feature[]> operator);

    FastJsonConfigSerializerBuilder<T> overrideReaderFeatures(JSONReader.Feature... features);

    FastJsonConfigSerializerBuilder<T> appendReaderFeatures(UnaryOperator<JSONReader.Feature[]> operator);

    <V> FastJsonConfigSerializerBuilder<T> registerTypeWriter(Type type, ObjectWriter<V> writer);

    <V> FastJsonConfigSerializerBuilder<T> registerTypeReader(Type type, ObjectReader<V> reader);

    ConfigSerializer<T> build();
}