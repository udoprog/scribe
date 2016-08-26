package eu.toolchain.scribe;

public interface Module {
  <T> EntityMapperBuilder<T> register(final EntityMapperBuilder<T> builder);
}
