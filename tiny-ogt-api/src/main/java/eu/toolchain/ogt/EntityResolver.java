package eu.toolchain.ogt;

import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.entitybinding.EntityBinding;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.type.ExecutableType;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.typemapping.TypeMapping;

import java.util.List;
import java.util.Optional;

public interface EntityResolver {
    TypeMapping mapping(JavaType type);

    TypeMapping mapping(JavaType type, Annotations annotations);

    Optional<CreatorMethod> detectCreatorMethod(JavaType type);

    Optional<FieldReader> detectFieldReader(
        JavaType type, String fieldName, Optional<JavaType> knownType
    );

    Optional<EntityBinding> detectBinding(JavaType type);

    Optional<TypeMapping> detectValueType(JavaType type);

    Optional<String> detectFieldName(JavaType type, Annotations annotations);

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

    List<EntityField> detectExecutableFields(ExecutableType executable);
}
