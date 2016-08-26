package eu.toolchain.scribe;

import eu.toolchain.scribe.entitymapping.EntityFieldStreamEncoder;

public interface EntityFieldsStreamEncoder<Target> {
  void encodeStart(Target target);

  void encodeEnd(Target target);

  <Source> void encodeField(
      EntityFieldStreamEncoder<Target, Source> field, Context path, Source value, Target target
  );

  void encodeEmpty(Context path, Target target);
}
