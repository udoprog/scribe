package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.FieldMapping;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JacksonEntityDecoder implements EntityDecoder<JsonNode> {
    private final Map<String, JsonNode> value;
    private final JacksonTypeDecoder decoder;

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
    public Optional<Object> decodeField(FieldMapping field, Context path) {
        return Optional
            .ofNullable(value.get(field.name()))
            .map(n -> field.type().decode(decoder, path, n));
    }
}
