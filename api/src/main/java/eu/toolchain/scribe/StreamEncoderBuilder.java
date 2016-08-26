package eu.toolchain.scribe;

@FunctionalInterface
public interface StreamEncoderBuilder<T, O> {
  StreamEncoder<T, O> apply(
      EntityResolver resolver, JavaType type, StreamEncoderFactory<T> encoding
  );
}
