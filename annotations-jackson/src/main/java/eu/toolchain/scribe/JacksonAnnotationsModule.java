package eu.toolchain.scribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import eu.toolchain.scribe.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.scribe.creatormethod.StaticMethodCreatorMethod;
import eu.toolchain.scribe.fieldreader.AnnotatedFieldReader;
import eu.toolchain.scribe.subtype.AnnotationSubTypesResolver;
import eu.toolchain.scribe.typemapping.EntityEncodeValue;

import java.util.Optional;

public class JacksonAnnotationsModule implements Module {
  @Override
  public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
    builder
        .creatorMethodDetector(ConstructorCreatorMethod.forAnnotation(JsonCreator.class))
        .creatorMethodDetector(StaticMethodCreatorMethod.forAnnotation(JsonCreator.class));

    builder.subTypesDetector(
        AnnotationSubTypesResolver.forAnnotation(JsonSubTypes.class, JsonSubTypes::value,
            t -> JavaType.of(t.value()),
            t -> "".equals(t.name()) ? Optional.empty() : Optional.of(t.name())));

    builder.encodeValueDetector(EntityEncodeValue.forAnnotation(JsonValue.class));

    builder.fieldReaderDetector(
        AnnotatedFieldReader.forAnnotation(JsonGetter.class, JsonGetter::value));

    builder.fieldNameDetector((resolver, type, annotations) -> annotations
        .getAnnotation(JsonProperty.class)
        .map(JsonProperty::value)
        .map(Match.withPriority(MatchPriority.HIGH)));

    builder.typeNameDetector((resolver, type) -> type
        .getAnnotation(JsonTypeName.class)
        .map(JsonTypeName::value)
        .map(Match.withPriority(MatchPriority.HIGH)));

    return builder;
  }
}
