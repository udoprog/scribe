package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.EntityBinding;
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

    Optional<EntityBinding> detectBinding(JavaType type);

    List<CreatorField> setupCreatorFields(Executable executable);

    CreatorField setupCreatorField(
        Annotations annotations, Optional<JavaType> fieldType, Optional<String> fieldName
    );

    Optional<TypeMapping> detectValueType(JavaType type);

    Optional<String> detectPropertyName(JavaType type, CreatorField field);

    Optional<String> detectName(JavaType type);

    <T> TypeEncodingProvider<T> providerFor(final EncodingFactory<T> factory);

    /**
     * Detect immediate field annotations for the given field, if present.
     *
     * @param type The type to detect field annotations for.
     * @param name The name of the field to detect annotations for.
     * @return Annotations for the given field.
     */
    Annotations detectFieldAnnotations(JavaType type, String name);
}
