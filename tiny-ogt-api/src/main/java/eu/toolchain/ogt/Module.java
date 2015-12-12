package eu.toolchain.ogt;

public interface Module {
    <T> EntityMapperBuilder<T> register(final EntityMapperBuilder<T> builder);
}
