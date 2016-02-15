package eu.toolchain.ogt;

public interface EncodingFactory<T> {
    FieldDecoder<T> fieldDecoder();

    FieldEncoder<T> fieldEncoder();
}
