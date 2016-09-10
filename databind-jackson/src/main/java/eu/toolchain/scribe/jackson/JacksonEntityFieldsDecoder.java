package eu.toolchain.scribe.jackson;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.EntityFieldDecoder;
import eu.toolchain.scribe.EntityFieldsDecoder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class JacksonEntityFieldsDecoder implements EntityFieldsDecoder<JsonNode> {
  private final Map<String, JsonNode> value;

  @Override
  public <Source> Decoded<Source> decodeField(
      final EntityFieldDecoder<JsonNode, Source> decoder, final Context path
  ) {
    return decoder.decodeOptionally(path, Decoded.ofNullable(value.get(decoder.getName())));
  }
}
