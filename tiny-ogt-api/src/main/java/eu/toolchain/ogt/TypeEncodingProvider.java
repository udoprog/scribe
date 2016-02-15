package eu.toolchain.ogt;

public interface TypeEncodingProvider<T> {
    TypeEncoding<Object, T> encodingFor(final JavaType type);

    <O> TypeEncoding<O, T> encodingFor(final Class<O> type);
}
