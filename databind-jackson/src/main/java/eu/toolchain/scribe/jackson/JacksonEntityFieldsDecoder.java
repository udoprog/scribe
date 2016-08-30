package eu.toolchain.scribe.jackson;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.EntityFieldDecoder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JacksonEntityFieldsDecoder implements EntityFieldsDecoder<JsonNode> {
  public static final JsonNode.Visitor<Optional<String>> MAP_TYPE =
      new JsonNode.Visitor<Optional<String>>() {
        @Override
        public Optional<String> visitString(final JsonNode.StringJsonNode string) {
          return Optional.of(string.getValue());
        }

        @Override
        public Optional<String> visitNull(final JsonNode.NullJsonNode n) {
          return Optional.empty();
        }
      };

  private final Map<String, JsonNode> value;

  @Override
  public <Source> Decoded<Source> decodeField(
      final EntityFieldDecoder<JsonNode, Source> decoder, final Context path
  ) {
    return decoder.decodeOptionally(path, Decoded.ofNullable(value.get(decoder.getName())));
  }
}
