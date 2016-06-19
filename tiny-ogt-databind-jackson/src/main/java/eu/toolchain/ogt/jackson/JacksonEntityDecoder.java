package eu.toolchain.ogt.jackson;

import eu.toolchain.ogt.Context;
import eu.toolchain.ogt.EntityDecoder;
import eu.toolchain.ogt.entitybinding.EntityFieldDecoder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JacksonEntityDecoder implements EntityDecoder<JsonNode> {
    private final Map<String, JsonNode> value;

    @Override
    public Optional<String> decodeType() {
        return Optional
            .ofNullable(value.get("type"))
            .map(v -> v.visit(new JsonNode.Visitor<String>() {
                @Override
                public String visitString(final JsonNode.StringJsonNode string) {
                    return string.getValue();
                }
            }));
    }

    @Override
    public Optional<Object> decodeField(
        final EntityFieldDecoder<JsonNode, Object> entityFieldEncoder, final Context path
    ) {
        return Optional
            .ofNullable(value.get(entityFieldEncoder.getName()))
            .map(n -> entityFieldEncoder.decode(path, n));
    }
}
