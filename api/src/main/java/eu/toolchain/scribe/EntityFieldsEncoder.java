package eu.toolchain.scribe;

public interface EntityFieldsEncoder<Target, EntityTarget> {
  <Source> void encodeField(EntityFieldEncoder<Target, Source> field, Context path, Source value);

  EntityTarget buildEmpty(Context path);

  EntityTarget build();
}
