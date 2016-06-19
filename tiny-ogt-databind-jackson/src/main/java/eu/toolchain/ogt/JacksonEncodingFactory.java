package eu.toolchain.ogt;

import eu.toolchain.ogt.encoding.DoubleDecoder;
import eu.toolchain.ogt.encoding.DoubleEncoder;
import eu.toolchain.ogt.encoding.FloatDecoder;
import eu.toolchain.ogt.encoding.FloatEncoder;
import eu.toolchain.ogt.encoding.IntegerDecoder;
import eu.toolchain.ogt.encoding.IntegerEncoder;
import eu.toolchain.ogt.encoding.ListDecoder;
import eu.toolchain.ogt.encoding.ListEncoder;
import eu.toolchain.ogt.encoding.LongDecoder;
import eu.toolchain.ogt.encoding.LongEncoder;
import eu.toolchain.ogt.encoding.MapDecoder;
import eu.toolchain.ogt.encoding.MapEncoder;
import eu.toolchain.ogt.encoding.ShortDecoder;
import eu.toolchain.ogt.encoding.ShortEncoder;
import eu.toolchain.ogt.encoding.StringDecoder;
import eu.toolchain.ogt.encoding.StringEncoder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.toolchain.ogt.TypeMatcher.any;
import static eu.toolchain.ogt.TypeMatcher.exact;
import static eu.toolchain.ogt.TypeMatcher.isPrimitive;
import static eu.toolchain.ogt.TypeMatcher.parameterized;

public class JacksonEncodingFactory implements EncodingFactory<JsonNode> {
    private static EncodingRegistry<JsonNode> registry = new EncodingRegistry<>();

    static {
        registry.encoder(parameterized(Map.class, any(), any()), (resolver, type, factory) -> {
            final ParameterizedType pt = (ParameterizedType) type;

            if (!String.class.equals(pt.getActualTypeArguments()[0])) {
                throw new IllegalArgumentException(
                    "First type argument must be String (" + type + ")");
            }

            final Encoder<JsonNode, Object> value = resolver
                .mapping(pt.getActualTypeArguments()[1])
                .newEncoderImmediate(resolver, factory);
            return new MapEncoder<>(value);
        });

        registry.encoder(parameterized(List.class, any()), (resolver, type, factory) -> {
            final ParameterizedType pt = (ParameterizedType) type;

            final Encoder<JsonNode, Object> value = resolver
                .mapping(pt.getActualTypeArguments()[0])
                .newEncoderImmediate(resolver, factory);

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
            final ParameterizedType pt = (ParameterizedType) type;

            if (!String.class.equals(pt.getActualTypeArguments()[0])) {
                throw new IllegalArgumentException(
                    "First type argument must be String (" + type + ")");
            }

            final Decoder<JsonNode, Object> value = resolver
                .mapping(pt.getActualTypeArguments()[1])
                .newDecoderImmediate(resolver, factory);
            return new MapDecoder<>(value);
        });

        registry.decoder(parameterized(List.class, any()), (resolver, type, factory) -> {
            final ParameterizedType pt = (ParameterizedType) type;

            final Decoder<JsonNode, Object> value = resolver
                .mapping(pt.getActualTypeArguments()[0])
                .newDecoderImmediate(resolver, factory);

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
    public <O> Stream<Encoder<JsonNode, O>> newEncoder(
        final EntityResolver resolver, final Type type
    ) {
        return registry.newEncoder(resolver, type, this);
    }

    @Override
    public <O> Stream<Decoder<JsonNode, O>> newDecoder(
        final EntityResolver resolver, final Type type
    ) {
        return registry.newDecoder(resolver, type, this);
    }

    @Override
    public EntityEncoder<JsonNode> newEntityEncoder() {
        return new JacksonEntityEncoder();
    }

    @Override
    public EntityDecoder<JsonNode> newEntityDecoder(final JsonNode instance) {
        return new JacksonEntityDecoder(
            instance.visit(new JsonNode.Visitor<Map<String, JsonNode>>() {
                @Override
                public Map<String, JsonNode> visitObject(final JsonNode.ObjectJsonNode object) {
                    return object.getFields();
                }
            }));
    }
}
