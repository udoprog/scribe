package eu.toolchain.ogt;

import java.lang.reflect.Executable;
import java.util.List;
import java.util.Optional;

import eu.toolchain.ogt.binding.Binding;
import eu.toolchain.ogt.creatormethod.CreatorField;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.TypeMapping;

public interface EntityResolver {
    TypeMapping mapping(Class<?> input);

    TypeMapping mapping(JavaType type);

    Optional<CreatorMethod> detectCreatorMethod(JavaType type);

    Optional<FieldReader> detectFieldReader(JavaType type, JavaType returnType, String fieldName);

    Optional<Binding> detectBinding(JavaType type);

    List<CreatorField> setupCreatorFields(Executable executable);

    Optional<String> detectPropertyName(JavaType type, CreatorField field);

    Optional<String> detectName(JavaType type);

    <T> TypeEncodingProvider<T> providerFor(final EncodingFactory<T> factory);
}
