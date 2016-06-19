package eu.toolchain.ogt.typesafe;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.TypeEncodingProvider;
import eu.toolchain.ogt.TypeReference;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class TypeSafeEntityMapper {
    private final TypeEncodingProvider<ConfigValue> parent;

    public TypeSafeEntityMapper(final EntityResolver resolver) {
        this.parent = resolver.providerFor(new TypeSafeEncodingFactory());
    }

    public TypeSafeEncoding<Object> encodingForType(final Type type) {
        return new TypeSafeEncoding<>(parent.newEncoder(type), parent.newDecoder(type));
    }

    public <T> TypeSafeEncoding<T> encodingFor(final Class<T> type) {
        return new TypeSafeEncoding<>(parent.newEncoder(type), parent.newDecoder(type));
    }

    public <T> TypeSafeEncoding<T> encodingFor(final TypeReference<T> type) {
        return new TypeSafeEncoding<>(parent.newEncoder(type), parent.newDecoder(type));
    }
}
