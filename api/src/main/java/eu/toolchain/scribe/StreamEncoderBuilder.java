package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

@FunctionalInterface
public interface StreamEncoderBuilder<T, O> {
  StreamEncoder<T, O> apply(
      EntityResolver resolver, JavaType type, StreamEncoderFactory<T> encoding
  );
}
