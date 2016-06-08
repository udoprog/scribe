package eu.toolchain.ogt;

import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.EntityTypeName;
import eu.toolchain.ogt.annotations.Property;
import eu.toolchain.ogt.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.ogt.creatormethod.StaticMethodCreatorMethod;
import eu.toolchain.ogt.subtype.NativeEntitySubTypesResolver;
import eu.toolchain.ogt.type.EntityValueTypeMapping;

import static java.util.Optional.ofNullable;

public class NativeAnnotationsModule implements Module {
    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
        return builder
            .registerCreatorMethod(ConstructorCreatorMethod.forAnnotation(EntityCreator.class))
            .registerCreatorMethod(StaticMethodCreatorMethod.forAnnotation(EntityCreator.class))
            .registerSubTypes(NativeEntitySubTypesResolver::detect)
            .registerValueType(EntityValueTypeMapping.forAnnotation(EntityCreator.class))
            .registerPropertyNameDetector((resolver, type, field) -> {
                return field.annotations().getAnnotation(Property.class).map(Property::value);
            })
            .registerNameDetector((resolver, type) -> {
                return ofNullable(type.getRawClass().getAnnotation(EntityTypeName.class)).map(
                    EntityTypeName::value);
            });
    }
}
