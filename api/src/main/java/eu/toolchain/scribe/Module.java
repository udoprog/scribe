package eu.toolchain.scribe;

@FunctionalInterface
public interface Module {
  void register(final EntityMapperBuilder builder);
}
