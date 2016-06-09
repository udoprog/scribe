package eu.toolchain.ogt;

public interface EncodingFactory<T> {
    TypeDecoder<T> fieldDecoder();

    TypeEncoder<T> fieldEncoder();
}
