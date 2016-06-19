package eu.toolchain.ogt.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.TypeEncodingProvider;
import eu.toolchain.ogt.TypeReference;
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

    public JacksonEncoding<Object> encodingForType(final Type type) {
        return new JacksonEncoding<>(parent.newEncoder(type), parent.newDecoder(type), factory);
    }

    public <T> JacksonEncoding<T> encodingFor(final Class<T> type) {
        return new JacksonEncoding<>(parent.newEncoder(type), parent.newDecoder(type), factory);
    }

    public <T> JacksonEncoding<T> encodingFor(final TypeReference<T> type) {
        return new JacksonEncoding<>(parent.newEncoder(type), parent.newDecoder(type), factory);
    }
}
