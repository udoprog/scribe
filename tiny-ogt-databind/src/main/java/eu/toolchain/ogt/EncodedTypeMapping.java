package eu.toolchain.ogt;

import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.typemapping.TypeMapping;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class EncodedTypeMapping implements TypeMapping {
    private final JavaType type;

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final List<Encoder<Target, Source>> results =
            factory.<Source>newEncoder(resolver, type).collect(Collectors.toList());

        if (results.size() > 1) {
            throw new IllegalArgumentException(
                "Type (" + type + ") has more than one matching encoder: " + results);
        }

        return results.stream().findFirst();
    }

    @Override
    public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        final List<Decoder<Target, Source>> results =
            factory.<Source>newDecoder(resolver, type).collect(Collectors.toList());

        if (results.size() > 1) {
            throw new IllegalArgumentException(
                "Type (" + type + ") has more than one matching decoder: " + results);
        }

        return results.stream().findFirst();
    }
}
