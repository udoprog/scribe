package eu.toolchain.scribe;

public interface ClassEncoding<Source> {
  <Target, EntityTarget> EntityEncoder<Target, EntityTarget, Source> newEntityEncoder(
      EntityResolver resolver, EncoderFactory<Target, EntityTarget> factory
  );

  <Target> EntityStreamEncoder<Target, Source> newEntityStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target, EntityTarget> EntityDecoder<Target, EntityTarget, Source> newEntityDecoder(
      EntityResolver resolver, DecoderFactory<Target, EntityTarget> factory
  );
}
