package eu.toolchain.ogt.typesafe;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EncodingRegistry;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.EntityEncoder;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.typesafe.encoding.DoubleDecoder;
import eu.toolchain.ogt.typesafe.encoding.DoubleEncoder;
import eu.toolchain.ogt.typesafe.encoding.FloatDecoder;
import eu.toolchain.ogt.typesafe.encoding.FloatEncoder;
import eu.toolchain.ogt.typesafe.encoding.IntegerDecoder;
import eu.toolchain.ogt.typesafe.encoding.IntegerEncoder;
import eu.toolchain.ogt.typesafe.encoding.ListDecoder;
import eu.toolchain.ogt.typesafe.encoding.ListEncoder;
import eu.toolchain.ogt.typesafe.encoding.LongDecoder;
import eu.toolchain.ogt.typesafe.encoding.LongEncoder;
import eu.toolchain.ogt.typesafe.encoding.MapEncoder;
import eu.toolchain.ogt.typesafe.encoding.ShortDecoder;
import eu.toolchain.ogt.typesafe.encoding.ShortEncoder;
import eu.toolchain.ogt.typesafe.encoding.StringEncoder;
import eu.toolchain.ogt.typesafe.encoding.MapDecoder;
import eu.toolchain.ogt.typesafe.encoding.StringDecoder;
import eu.toolchain.ogt.type.JavaType;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.toolchain.ogt.TypeMatcher.any;
import static eu.toolchain.ogt.TypeMatcher.exact;
import static eu.toolchain.ogt.TypeMatcher.isPrimitive;
import static eu.toolchain.ogt.TypeMatcher.parameterized;

public class TypeSafeEncodingFactory implements EncodingFactory<ConfigValue> {
    private static EncodingRegistry<ConfigValue> registry = new EncodingRegistry<>();

    static {
        registry.encoder(parameterized(Map.class, any(), any()), (resolver, type, factory) -> {
            final JavaType first = type.getTypeParameter(0).get();
            final JavaType second = type.getTypeParameter(1).get();

            if (!JavaType.of(String.class).equals(first)) {
                throw new IllegalArgumentException(
                    "First type argument must be String (" + type + ")");
            }

            final Encoder<ConfigValue, Object> value =
                resolver.mapping(second).newEncoderImmediate(resolver, factory);
            return new MapEncoder<>(value);
        });

        registry.encoder(parameterized(List.class, any()), (resolver, type, factory) -> {
            final JavaType first = type.getTypeParameter(0).get();

            final Encoder<ConfigValue, Object> value =
                resolver.mapping(first).newEncoderImmediate(resolver, factory);

            return new ListEncoder<>(value);
        });

        registry.encoder(exact(String.class), (resolver, type, factory) -> new StringEncoder());

        registry.encoder(isPrimitive(Short.class), (resolver, type, factory) -> ShortEncoder.get());
        registry.encoder(isPrimitive(Integer.class),
            (resolver, type, factory) -> IntegerEncoder.get());
        registry.encoder(isPrimitive(Long.class), (resolver, type, factory) -> LongEncoder.get());

        registry.encoder(isPrimitive(Float.class), (resolver, type, factory) -> FloatEncoder.get());

        registry.encoder(isPrimitive(Double.class),
            (resolver, type, factory) -> DoubleEncoder.get());
    }

    static {
        registry.decoder(parameterized(Map.class, any(), any()), (resolver, type, factory) -> {
            final JavaType first = type.getTypeParameter(0).get();
            final JavaType second = type.getTypeParameter(1).get();

            if (!JavaType.of(String.class).equals(first)) {
                throw new IllegalArgumentException(
                    "First type argument must be String (" + type + ")");
            }

            final Decoder<ConfigValue, Object> value =
                resolver.mapping(second).newDecoderImmediate(resolver, factory);
            return new MapDecoder<>(value);
        });

        registry.decoder(parameterized(List.class, any()), (resolver, type, factory) -> {
            final JavaType first = type.getTypeParameter(0).get();

            final Decoder<ConfigValue, Object> value =
                resolver.mapping(first).newDecoderImmediate(resolver, factory);

            return new ListDecoder<>(value);
        });

        registry.decoder(exact(String.class), (resolver, type, factory) -> new StringDecoder());

        registry.decoder(isPrimitive(Short.class), (resolver, type, factory) -> ShortDecoder.get());
        registry.decoder(isPrimitive(Integer.class),
            (resolver, type, factory) -> IntegerDecoder.get());
        registry.decoder(isPrimitive(Long.class), (resolver, type, factory) -> LongDecoder.get());

        registry.decoder(isPrimitive(Float.class), (resolver, type, factory) -> FloatDecoder.get());

        registry.decoder(isPrimitive(Double.class),
            (resolver, type, factory) -> DoubleDecoder.get());
    }

    @Override
    public <O> Stream<Encoder<ConfigValue, O>> newEncoder(
        final EntityResolver resolver, final JavaType type
    ) {
        return registry.newEncoder(resolver, type, this);
    }

    @Override
    public <O> Stream<Decoder<ConfigValue, O>> newDecoder(
        final EntityResolver resolver, final JavaType type
    ) {
        return registry.newDecoder(resolver, type, this);
    }

    @Override
    public EntityEncoder<ConfigValue> newEntityEncoder() {
        return new TypeSafeEntityEncoder();
    }

    @Override
    public EntityDecoder<ConfigValue> newEntityDecoder(final ConfigValue instance) {
        final Map<String, ConfigValue> values;

        switch (instance.valueType()) {
            case OBJECT:
                values = ((ConfigObject) instance);
                break;
            default:
                throw new IllegalArgumentException("Expected object: " + instance);
        }

        return new TypeSafeEntityDecoder(values);
    }
}
