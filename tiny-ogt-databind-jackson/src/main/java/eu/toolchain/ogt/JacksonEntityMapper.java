package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonFactory;
import lombok.Data;

@Data
public class JacksonEntityMapper {
    private final TypeEncodingProvider<JsonNode> parent;
    private final JsonFactory factory;

    public JacksonEntityMapper(final EntityResolver resolver, final JsonFactory factory) {
        this.parent = resolver.providerFor(new JacksonEncodingFactory());
        this.factory = factory;
    }

    public JacksonTypeEncoding<Object> encodingFor(final JavaType type) {
        return new JacksonTypeEncoding<Object>(parent.encodingFor(type), factory);
    }

    public <T> JacksonTypeEncoding<T> encodingFor(final Class<T> type) {
        return new JacksonTypeEncoding<T>(parent.encodingFor(type), factory);
    }
}
