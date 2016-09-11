package eu.toolchain.scribe;

import eu.toolchain.scribe.annotations.EntityCreator;
import eu.toolchain.scribe.annotations.EntitySubTypes;
import eu.toolchain.scribe.annotations.EntityTypeName;
import eu.toolchain.scribe.annotations.EntityValue;
import eu.toolchain.scribe.annotations.FieldGetter;
import eu.toolchain.scribe.annotations.Property;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.Optional;

public class NativeAnnotationsModule implements Module {
  @Override
  public void register(ScribeBuilder b) {
    b
        .instanceBuilder(ConstructorClassInstanceBuilder.forAnnotation(EntityCreator.class))
        .instanceBuilder(StaticMethodClassInstanceBuilder.forAnnotation(EntityCreator.class));

    b.subTypes(AnnotationSubTypesResolver.forAnnotation(EntitySubTypes.class, EntitySubTypes::value,
        e -> JavaType.of(e.value()), e -> Optional.empty()));

    b.encodeValue(EntityEncodeValue.forAnnotation(EntityValue.class));

    b
        .decodeValue(ConstructorEntityDecodeValue.forAnnotation(EntityCreator.class))
        .decodeValue(StaticMethodEntityDecodeValue.forAnnotation(EntityCreator.class));

    b.fieldReader(AnnotatedFieldReader.forAnnotation(FieldGetter.class, FieldGetter::value));

    b.fieldName((resolver, type, annotations) -> annotations
        .getAnnotation(Property.class)
        .map(Property::value)
        .map(Match.withPriority(MatchPriority.HIGH)));

    b.typeName((resolver, type) -> type
        .getAnnotation(EntityTypeName.class)
        .map(EntityTypeName::value)
        .map(Match.withPriority(MatchPriority.HIGH)));
  }
}
