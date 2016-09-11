package eu.toolchain.scribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.Optional;

public class JacksonAnnotationsModule implements Module {
  @Override
  public void register(ScribeBuilder builder) {
    builder
        .instanceBuilder(ConstructorClassInstanceBuilder.forAnnotation(JsonCreator.class))
        .instanceBuilder(StaticMethodClassInstanceBuilder.forAnnotation(JsonCreator.class));

    builder.subTypes(
        AnnotationSubTypesResolver.forAnnotation(JsonSubTypes.class, JsonSubTypes::value,
            t -> JavaType.of(t.value()),
            t -> "".equals(t.name()) ? Optional.empty() : Optional.of(t.name())));

    builder.encodeValue(EntityEncodeValue.forAnnotation(JsonValue.class));

    builder.fieldReader(
        AnnotatedFieldReader.forAnnotation(JsonGetter.class, JsonGetter::value));

    builder.fieldName((resolver, type, annotations) -> annotations
        .getAnnotation(JsonProperty.class)
        .map(JsonProperty::value)
        .map(Match.withPriority(MatchPriority.HIGH)));

    builder.typeName((resolver, type) -> type
        .getAnnotation(JsonTypeName.class)
        .map(JsonTypeName::value)
        .map(Match.withPriority(MatchPriority.HIGH)));
  }
}
