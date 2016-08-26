package eu.toolchain.scribe;

import eu.toolchain.scribe.entitymapping.EntityFieldEncoder;

public interface EntityFieldsEncoder<Target> {
  <Source> void encodeField(EntityFieldEncoder<Target, Source> field, Context path, Source value);

  Target buildEmpty(Context path);

  Target build();
}
