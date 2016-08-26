package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.EntityEncoder;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.EntityStreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;

import java.util.List;

public interface EntityMapping {
  List<? extends EntityFieldMapping> fields();

  <Target> EntityEncoder<Target, Object> newEntityTypeEncoder(
      EntityResolver resolver, EncoderFactory<Target> factory
  );

  <Target> EntityStreamEncoder<Target, Object> newEntityTypeStreamEncoder(
      EntityResolver resolver, StreamEncoderFactory<Target> factory
  );

  <Target> EntityDecoder<Target, Object> newEntityTypeDecoder(
      EntityResolver resolver, DecoderFactory<Target> factory
  );
}
