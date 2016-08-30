package eu.toolchain.scribe;

public interface ClassEncoding {
  <Target> EntityEncoder<Target, Object> newEntityEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  );

  <Target> EntityStreamEncoder<Target, Object> newEntityStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target> EntityDecoder<Target, Object> newEntityDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  );
}
