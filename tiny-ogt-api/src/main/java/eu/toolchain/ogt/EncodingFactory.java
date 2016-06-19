package eu.toolchain.ogt;

import eu.toolchain.ogt.type.JavaType;

import java.util.stream.Stream;

public interface EncodingFactory<T> {
    <O> Stream<Encoder<T, O>> newEncoder(EntityResolver resolver, JavaType type);

    <O> Stream<Decoder<T, O>> newDecoder(EntityResolver resolver, JavaType type);

    EntityEncoder<T> newEntityEncoder();

    EntityDecoder<T> newEntityDecoder(T instance);
}
