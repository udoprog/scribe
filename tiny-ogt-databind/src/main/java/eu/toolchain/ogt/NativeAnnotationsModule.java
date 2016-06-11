package eu.toolchain.ogt;

import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.EntityTypeName;
import eu.toolchain.ogt.annotations.FieldGetter;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.ogt.creatormethod.StaticMethodCreatorMethod;
import eu.toolchain.ogt.fieldreader.AnnotatedFieldReader;
import eu.toolchain.ogt.subtype.NativeEntitySubTypesResolver;
import eu.toolchain.ogt.type.EntityValueTypeMapping;

import static java.util.Optional.ofNullable;

public class NativeAnnotationsModule implements Module {
    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
        return builder
            .creatorMethodDetector(ConstructorCreatorMethod.forAnnotation(EntityCreator.class))
            .creatorMethodDetector(StaticMethodCreatorMethod.forAnnotation(EntityCreator.class))
            .subTypesDetector(NativeEntitySubTypesResolver::detect)
            .valueTypeDetector(EntityValueTypeMapping.forAnnotation(EntityCreator.class))
            .fieldReaderDetector(AnnotatedFieldReader.of(FieldGetter.class, FieldGetter::value))
            .fieldNameDetector((resolver, type, field) -> {
                return field.getAnnotations().getAnnotation(Property.class).map(Property::value);
            })
            .typeNameDetector((resolver, type) -> {
                return ofNullable(type.getRawClass().getAnnotation(EntityTypeName.class)).map(
                    EntityTypeName::value);
            });
    }
}
