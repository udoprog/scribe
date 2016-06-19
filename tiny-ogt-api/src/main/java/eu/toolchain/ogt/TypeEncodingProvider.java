package eu.toolchain.ogt;

import java.lang.reflect.Type;

public interface TypeEncodingProvider<Target> {
    Encoder<Target, Object> newEncoder(final Type type);

    <Source> Encoder<Target, Source> newEncoder(final Class<Source> type);

    <Source> Encoder<Target, Source> newEncoder(final TypeReference<Source> type);

    Decoder<Target, Object> newDecoder(final Type type);

    <Source> Decoder<Target, Source> newDecoder(final Class<Source> type);

    <Source> Decoder<Target, Source> newDecoder(final TypeReference<Source> type);
}
