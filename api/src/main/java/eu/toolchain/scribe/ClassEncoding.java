package eu.toolchain.scribe;

public interface ClassEncoding {
  <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Object> newEntityEncoder(
      EntityResolver resolver, EncoderFactory<Target, EntityTarget> factory
  );

  <Target> EntityStreamEncoder<Target, Object> newEntityStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target, EntityTarget> EntityDecoder<Target, EntityTarget, Object> newEntityDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory
  );
}
