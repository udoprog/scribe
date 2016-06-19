package eu.toolchain.ogt.typemapping;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.EntityTypeDecoder;
import eu.toolchain.ogt.EntityTypeEncoder;
import lombok.Data;

import java.util.Optional;

public interface EntityTypeMapping extends TypeMapping {
    default Optional<String> typeName() {
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    default <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final EntityTypeEncoder<Target, Object> encoding = newEntityTypeEncoder(resolver, factory);
        return Optional.of((Encoder<Target, Source>) new EntityEncoder(encoding, factory));
    }

    @SuppressWarnings("unchecked")
    @Override
    default <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
        EntityResolver resolver, EncodingFactory<Target> factory
    ) {
        final EntityTypeDecoder<Target, Object> decoder = newEntityTypeDecoder(resolver, factory);
        return Optional.of((Decoder<Target, Source>) new EntityDecoder(decoder, factory));
    }

    <Target> EntityTypeEncoder<Target, Object> newEntityTypeEncoder(
        EntityResolver resolver, EncodingFactory<Target> factory
    );

    <Target> EntityTypeDecoder<Target, Object> newEntityTypeDecoder(
        EntityResolver resolver, EncodingFactory<Target> factory
    );

    @Data
    class EntityEncoder<T> implements Encoder<T, Object> {
        private final EntityTypeEncoder<T, Object> encoder;
        private final EncodingFactory<T> factory;

        @Override
        public T encode(final Context path, final Object instance) {
            return encoder.encode(factory.newEntityEncoder(), path, instance);
        }
    }

    @Data
    class EntityDecoder<T> implements Decoder<T, Object> {
        private final EntityTypeDecoder<T, Object> decoder;
        private final EncodingFactory<T> factory;

        @Override
        public Object decode(final Context path, final T instance) {
            return decoder.decode(factory.newEntityDecoder(instance), path);
        }
    }
}
