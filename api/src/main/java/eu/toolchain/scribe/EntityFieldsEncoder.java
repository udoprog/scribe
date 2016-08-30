package eu.toolchain.scribe;

public interface EntityFieldsEncoder<Target> {
  <Source> void encodeField(EntityFieldEncoder<Target, Source> field, Context path, Source value);

  Target buildEmpty(Context path);

  Target build();
}
