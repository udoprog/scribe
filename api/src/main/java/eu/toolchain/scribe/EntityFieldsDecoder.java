package eu.toolchain.scribe;

public interface EntityFieldsDecoder<Target> {
  <Source> Decoded<Source> decodeField(EntityFieldDecoder<Target, Source> decoder, Context path);
}
