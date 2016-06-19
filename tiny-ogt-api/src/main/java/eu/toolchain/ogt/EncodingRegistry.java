package eu.toolchain.ogt;

import eu.toolchain.ogt.type.JavaType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EncodingRegistry<Target> {
    private final List<EncoderPair<Target, ?>> encoders = new ArrayList<>();
    private final List<DecoderPair<Target, ?>> decoders = new ArrayList<>();

    public <Source> void encoder(
        final TypeMatcher matcher, final EncoderBuilder<Target, Source> encoder
    ) {
        this.encoders.add(new EncoderPair<>(matcher, encoder));
    }

    public <Source> void decoder(
        final TypeMatcher matcher, final DecoderBuilder<Target, Source> decoder
    ) {
        this.decoders.add(new DecoderPair<>(matcher, decoder));
    }

    @SuppressWarnings("unchecked")
    public <Source> Stream<Encoder<Target, Source>> newEncoder(
        final EntityResolver resolver, final JavaType type, final EncodingFactory<Target> encoding
    ) {
        return encoders
            .stream()
            .filter(p -> p.matcher.matches(type))
            .map(p -> (Encoder<Target, Source>) p.encoder.apply(resolver, type, encoding));
    }

    @SuppressWarnings("unchecked")
    public <Source> Stream<Decoder<Target, Source>> newDecoder(
        final EntityResolver resolver, final JavaType type, final EncodingFactory<Target> encoding
    ) {
        return decoders
            .stream()
            .filter(p -> p.matcher.matches(type))
            .map(p -> (Decoder<Target, Source>) p.decoder.apply(resolver, type, encoding));
    }

    @Data
    static class EncoderPair<T, O> {
        private final TypeMatcher matcher;
        private final EncoderBuilder<T, O> encoder;
    }

    @Data
    static class DecoderPair<T, O> {
        private final TypeMatcher matcher;
        private final DecoderBuilder<T, O> decoder;
    }

    @FunctionalInterface
    public interface EncoderBuilder<T, O> {
        Encoder<T, O> apply(
            EntityResolver resolver, JavaType type, EncodingFactory<T> encoding
        );
    }

    @FunctionalInterface
    public interface DecoderBuilder<T, O> {
        Decoder<T, O> apply(
            EntityResolver resolver, JavaType type, EncodingFactory<T> encoding
        );
    }
}
