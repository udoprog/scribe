package eu.toolchain.ogt;

public interface EncodingFactory<T> {
    EntityDecoder entityDecoder(T input);

    BuildableEntityEncoder<T> entityEncoder();
}
