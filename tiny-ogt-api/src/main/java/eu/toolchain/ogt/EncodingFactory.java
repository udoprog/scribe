package eu.toolchain.ogt;

import java.lang.reflect.Type;
import java.util.stream.Stream;

public interface EncodingFactory<T> {
    <O> Stream<Encoder<T, O>> newEncoder(EntityResolver resolver, Type type);

    <O> Stream<Decoder<T, O>> newDecoder(EntityResolver resolver, Type type);

    EntityEncoder<T> newEntityEncoder();

    EntityDecoder<T> newEntityDecoder(T instance);
}
