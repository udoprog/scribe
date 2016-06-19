package eu.toolchain.ogt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import eu.toolchain.ogt.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.ogt.creatormethod.StaticMethodCreatorMethod;
import eu.toolchain.ogt.fieldreader.AnnotatedFieldReader;
import eu.toolchain.ogt.subtype.AnnotationSubTypesResolver;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.typemapping.EntityValueTypeMapping;

public class JacksonAnnotationsModule implements Module {
    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
        return builder
            .creatorMethodDetector(ConstructorCreatorMethod.forAnnotation(JsonCreator.class))
            .creatorMethodDetector(StaticMethodCreatorMethod.forAnnotation(JsonCreator.class))
            .subTypesDetector(
                AnnotationSubTypesResolver.forAnnotation(JsonSubTypes.class, JsonSubTypes::value,
                    t -> JavaType.of(t.value())))
            .valueTypeDetector(EntityValueTypeMapping.forAnnotation(JsonValue.class))
            .fieldReaderDetector(AnnotatedFieldReader.of(JsonGetter.class, JsonGetter::value))
            .fieldNameDetector((resolver, type, annotations) -> annotations
                .getAnnotation(JsonProperty.class)
                .map(JsonProperty::value)
                .map(Match.withPriority(Priority.HIGH)))
            .typeNameDetector((resolver, type) -> type
                .getAnnotation(JsonTypeName.class)
                .map(JsonTypeName::value)
                .map(Match.withPriority(Priority.HIGH)));
    }
}
