package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.Reflection;
import eu.toolchain.ogt.TypeMatcher;
import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class OptionalTypeMapping implements TypeMapping {
    private final TypeMapping component;

    private static final TypeMatcher MATCHER =
        TypeMatcher.parameterized(Optional.class, TypeMatcher.any());

    public static Stream<TypeMapping> detect(
        final EntityResolver resolver, final Type type
    ) {
        if (MATCHER.matches(type)) {
            final ParameterizedType pt = Reflection.asParameterizedType(type).get();
            final TypeMapping component = resolver.mapping(pt.getActualTypeArguments()[0]);
            return Stream.of(new OptionalTypeMapping(component));
        }

        return Stream.empty();
    }

    @Override
    public Type getType() {
        return component.getType();
    }

    @Override
    public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        return component.<Target, Source>newEncoder(resolver, factory).map(
            parent -> new Encoder<Target, Source>() {
                @Override
                public Target encode(final Context path, final Source instance) {
                    return parent.encode(path, instance);
                }

                @SuppressWarnings("unchecked")
                @Override
                public Optional<Object> asOptional(final Object object) {
                    return (Optional<Object>) object;
                }
            });
    }

    @Override
    public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        return component.<Target, Source>newDecoder(resolver, factory).map(
            parent -> new Decoder<Target, Source>() {
                @Override
                public Source decode(final Context path, final Target instance) {
                    return parent.decode(path, instance);
                }

                @Override
                public Optional<?> fromOptional(final Optional<?> value) {
                    return Optional.of(value);
                }
            });
    }
}
