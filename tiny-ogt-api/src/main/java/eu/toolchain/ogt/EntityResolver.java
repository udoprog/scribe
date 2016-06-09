package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.Binding;
import eu.toolchain.ogt.creatormethod.CreatorField;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.TypeMapping;

import java.lang.reflect.Executable;
import java.util.List;
import java.util.Optional;

public interface EntityResolver {
    TypeMapping mapping(Class<?> input);

    TypeMapping mapping(JavaType type);

    TypeMapping mapping(JavaType type, Annotations annotations);

    Optional<CreatorMethod> detectCreatorMethod(JavaType type);

    Optional<FieldReader> detectFieldReader(
        JavaType type, String fieldName, Optional<JavaType> knownType
    );

    Optional<Binding> detectBinding(JavaType type);

    List<CreatorField> setupCreatorFields(Executable executable);

    CreatorField setupCreatorField(
        Annotations annotations, Optional<JavaType> fieldType, Optional<String> fieldName
    );

    Optional<String> detectPropertyName(JavaType type, CreatorField field);

    Optional<String> detectName(JavaType type);

    <T> TypeEncodingProvider<T> providerFor(final EncodingFactory<T> factory);

    boolean isBytes(Annotations annotations);
}
