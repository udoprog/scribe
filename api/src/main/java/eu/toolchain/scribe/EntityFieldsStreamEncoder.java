package eu.toolchain.scribe;

public interface EntityFieldsStreamEncoder<Target> {
  void encodeStart(Context path, Target target);

  void encodeEnd(Context path, Target target);

  <Source> void encodeField(
      EntityFieldStreamEncoder<Target, Source> field, Context path, Source value, Target target
  );

  void encodeEmpty(Context path, Target target);
}
