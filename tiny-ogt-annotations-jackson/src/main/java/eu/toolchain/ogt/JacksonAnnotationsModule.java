package eu.toolchain.ogt;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import eu.toolchain.ogt.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.ogt.creatormethod.StaticMethodCreatorMethod;
import eu.toolchain.ogt.subtype.JacksonEntitySubTypesResolver;
import eu.toolchain.ogt.type.EntityValueTypeMapping;

public class JacksonAnnotationsModule implements Module {
    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
        return builder
                .registerCreatorMethod(ConstructorCreatorMethod.forAnnotation(JsonCreator.class))
                .registerCreatorMethod(StaticMethodCreatorMethod.forAnnotation(JsonCreator.class))
                .registerSubTypes(JacksonEntitySubTypesResolver::detect)
                .registerValueType(EntityValueTypeMapping.forAnnotation(JsonCreator.class))
                .registerPropertyNameDetector((resolver, type, field) -> {
                    return ofNullable(field.parameter().getAnnotation(JsonProperty.class))
                            .map(JsonProperty::value);
                }).registerNameDetector((resolver, type) -> {
                    return ofNullable(type.getRawClass().getAnnotation(JsonTypeName.class))
                            .map(JsonTypeName::value);
                });
    }
}
