package eu.toolchain.scribe;

@FunctionalInterface
public interface Module {
  void register(final ScribeBuilder builder);
}
