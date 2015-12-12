package eu.toolchain.ogt;

public interface TypeEncodingProvider<E> {
    public <T> TypeEncoding<T, E> encodingFor(Class<T> type);
}
