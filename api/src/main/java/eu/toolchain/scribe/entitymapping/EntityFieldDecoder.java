package eu.toolchain.scribe.entitymapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;

public interface EntityFieldDecoder<Target, Source> extends EntityFieldDescriptor {
  String getName();

  Decoded<Source> decode(Context path, Target instance);

  Decoded<Source> decodeOptionally(Context path, Decoded<Target> instance);
}
