package eu.toolchain.ogt;

public interface BuildableEntityEncoder<T> extends EntityEncoder {
    T build();
}
