package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;

import java.util.stream.Stream;

/**
 * Encapsulates information about a given Type that is used for resolving an encoder.
 *
 * @author udoprog
 */
public interface Mapping {
  JavaType getType();

  /**
   * Build a stream of new encoders for the current type mapping.
   * <p>
   * This method returns a stream because multiple encoders might match the current type.
   */
  <Target, Source> Stream<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory, Flags flags
  );

  default <Target, Source> Stream<Encoder<Target, Source>> newEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  ) {
    return newEncoder(resolver, factory, Flags.empty());
  }

  /**
   * Build a stream of new stream encoders for the current type mapping.
   * <p>
   * This method returns a stream because multiple stream encoders might match the current type.
   */
  <Target, Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory, Flags flags
  );

  default <Target, Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  ) {
    return newStreamEncoder(resolver, factory, Flags.empty());
  }

  /**
   * Build a stream of new decoders for the current type mapping.
   * <p>
   * This method returns a stream because multiple decoders might match the current type.
   */
  <Target, Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory, Flags flags
  );

  default <Target, Source> Stream<Decoder<Target, Source>> newDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  ) {
    return newDecoder(resolver, factory, Flags.empty());
  }

  /**
   * Lazy initialization of this mapper.
   * <p>
   * This method is called immediately after this mapping has been cached by the resolver. Any
   * dependent type parameters should be initialized in here to avoid indefinite circular
   * resolving.
   *
   * @param resolver Resolver to initialize using.
   */
  default void initialize(final EntityResolver resolver) {
  }
}
