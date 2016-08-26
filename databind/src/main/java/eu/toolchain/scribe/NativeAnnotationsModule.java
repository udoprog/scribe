package eu.toolchain.scribe;

import eu.toolchain.scribe.annotations.EntityCreator;
import eu.toolchain.scribe.annotations.EntitySubTypes;
import eu.toolchain.scribe.annotations.EntityTypeName;
import eu.toolchain.scribe.annotations.EntityValue;
import eu.toolchain.scribe.annotations.FieldGetter;
import eu.toolchain.scribe.annotations.Property;
import eu.toolchain.scribe.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.scribe.creatormethod.StaticMethodCreatorMethod;
import eu.toolchain.scribe.fieldreader.AnnotatedFieldReader;
import eu.toolchain.scribe.subtype.AnnotationSubTypesResolver;
import eu.toolchain.scribe.typemapping.ConstructorEntityDecodeValue;
import eu.toolchain.scribe.typemapping.EntityEncodeValue;
import eu.toolchain.scribe.typemapping.StaticMethodEntityDecodeValue;

import java.util.Optional;

public class NativeAnnotationsModule implements Module {
  @Override
  public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
    return builder
        .creatorMethodDetector(ConstructorCreatorMethod.forAnnotation(EntityCreator.class))
        .creatorMethodDetector(StaticMethodCreatorMethod.forAnnotation(EntityCreator.class))
        .subTypesDetector(
            AnnotationSubTypesResolver.forAnnotation(EntitySubTypes.class, EntitySubTypes::value,
                e -> JavaType.of(e.value()), e -> Optional.empty()))
        .encodeValueDetector(EntityEncodeValue.forAnnotation(EntityValue.class))
        .decodeValueDetector(ConstructorEntityDecodeValue.forAnnotation(EntityCreator.class))
        .decodeValueDetector(StaticMethodEntityDecodeValue.forAnnotation(EntityCreator.class))
        .fieldReaderDetector(
            AnnotatedFieldReader.forAnnotation(FieldGetter.class, FieldGetter::value))
        .fieldNameDetector((resolver, type, annotations) -> annotations
            .getAnnotation(Property.class)
            .map(Property::value)
            .map(Match.withPriority(MatchPriority.HIGH)))
        .typeNameDetector((resolver, type) -> type
            .getAnnotation(EntityTypeName.class)
            .map(EntityTypeName::value)
            .map(Match.withPriority(MatchPriority.HIGH)));
  }
}
