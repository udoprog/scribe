package eu.toolchain.ogt;

import com.fasterxml.jackson.core.JsonFactory;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class JacksonEntityMapper {
    private final TypeEncodingProvider<JsonNode> parent;
    private final JsonFactory factory;

    public JacksonEntityMapper(final EntityResolver resolver, final JsonFactory factory) {
        this.parent = resolver.providerFor(new JacksonEncodingFactory());
        this.factory = factory;
    }

    public JacksonTypeEncoding<Object> encodingForType(final Type type) {
        return new JacksonTypeEncoding<>(parent.newEncoder(type), parent.newDecoder(type), factory);
    }

    public <T> JacksonTypeEncoding<T> encodingFor(final Class<T> type) {
        return new JacksonTypeEncoding<>(parent.newEncoder(type), parent.newDecoder(type), factory);
    }

    public <T> JacksonTypeEncoding<T> encodingFor(final TypeReference<T> type) {
        return new JacksonTypeEncoding<>(parent.newEncoder(type), parent.newDecoder(type), factory);
    }
}
