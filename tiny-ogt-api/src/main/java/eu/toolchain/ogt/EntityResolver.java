package eu.toolchain.ogt;

import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.entitybinding.EntityBinding;
import eu.toolchain.ogt.fieldreader.FieldReader;
import eu.toolchain.ogt.typemapping.TypeMapping;

import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface EntityResolver {
    TypeMapping mapping(Type type);

    TypeMapping mapping(Type type, Annotations annotations);

    Optional<CreatorMethod> detectCreatorMethod(Type type);

    Optional<FieldReader> detectFieldReader(
        Type type, String fieldName, Optional<Type> knownType
    );

    Optional<EntityBinding> detectBinding(Type type);

    Optional<TypeMapping> detectValueType(Type type);

    Optional<String> detectFieldName(Type type, Annotations annotations);

    Optional<String> detectName(Type type);

    <T> TypeEncodingProvider<T> providerFor(final EncodingFactory<T> factory);

    /**
     * Detect immediate field annotations for the given field, if present.
     *
     * @param type The type to detect field annotations for.
     * @param name The name of the field to detect annotations for.
     * @return Annotations for the given field.
     */
    Annotations detectFieldAnnotations(Type type, String name);

    List<EntityField> detectExecutableFields(Executable executable);
}
