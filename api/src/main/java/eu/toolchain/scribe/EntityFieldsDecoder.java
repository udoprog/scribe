package eu.toolchain.scribe;

import eu.toolchain.scribe.entitymapping.EntityFieldDecoder;

public interface EntityFieldsDecoder<Target> {
  <Source> Decoded<Source> decodeField(EntityFieldDecoder<Target, Source> decoder, Context path);
}
