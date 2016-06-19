package eu.toolchain.ogt;

import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.EntitySubTypes;
import eu.toolchain.ogt.annotations.EntityTypeName;
import eu.toolchain.ogt.annotations.EntityValue;
import eu.toolchain.ogt.annotations.FieldGetter;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.ogt.creatormethod.StaticMethodCreatorMethod;
import eu.toolchain.ogt.fieldreader.AnnotatedFieldReader;
import eu.toolchain.ogt.subtype.AnnotationSubTypesResolver;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.typemapping.EntityValueTypeMapping;

public class NativeAnnotationsModule implements Module {
    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
        return builder
            .creatorMethodDetector(ConstructorCreatorMethod.forAnnotation(EntityCreator.class))
            .creatorMethodDetector(StaticMethodCreatorMethod.forAnnotation(EntityCreator.class))
            .subTypesDetector(AnnotationSubTypesResolver.forAnnotation(EntitySubTypes.class,
                EntitySubTypes::value, e -> JavaType.of(e.value())))
            .valueTypeDetector(EntityValueTypeMapping.forAnnotation(EntityValue.class))
            .fieldReaderDetector(AnnotatedFieldReader.of(FieldGetter.class, FieldGetter::value))
            .fieldNameDetector((resolver, type, annotations) -> annotations
                .getAnnotation(Property.class)
                .map(Property::value)
                .map(Match.withPriority(Priority.HIGH)))
            .typeNameDetector((resolver, type) -> type
                .getAnnotation(EntityTypeName.class)
                .map(EntityTypeName::value)
                .map(Match.withPriority(Priority.HIGH)));
    }
}
