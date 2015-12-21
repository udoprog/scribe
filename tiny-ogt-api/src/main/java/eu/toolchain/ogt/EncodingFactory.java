package eu.toolchain.ogt;

public interface EncodingFactory<T> {
    EntityDecoder entityDecoder(T input);

    EntityEncoder entityEncoder();

    FieldDecoder<?> fieldDecoder();

    FieldEncoder<?> fieldEncoder();
}
