package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.BuilderEntityBinding;
import eu.toolchain.ogt.binding.ConstructorEntityBinding;
import eu.toolchain.ogt.creatormethod.ConstructorPropertiesCreatorMethod;
import eu.toolchain.ogt.fieldreader.GetterFieldReader;
import eu.toolchain.ogt.type.ByteArrayTypeMapping;
import eu.toolchain.ogt.type.DateMapping;
import eu.toolchain.ogt.type.ListTypeMapping;
import eu.toolchain.ogt.type.MapTypeMapping;
import eu.toolchain.ogt.type.OptionalTypeMapping;
import eu.toolchain.ogt.type.StringTypeMapping;
import eu.toolchain.ogt.typeinterceptor.BoxedPrimitiveTypeInterceptor;
import eu.toolchain.ogt.typeinterceptor.BytesAnnotationTypeInterceptor;
import eu.toolchain.ogt.typeinterceptor.GenericInterceptor;
import eu.toolchain.ogt.typeinterceptor.ObjectArrayInterceptor;
import eu.toolchain.ogt.typeinterceptor.PrimitiveTypeInterceptor;
import eu.toolchain.ogt.typeinterceptor.RawInterceptor;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DefaultModule implements Module {
    public static final StringTypeMapping STRING_MAPPING = new StringTypeMapping();
    public static final DateMapping DATE_MAPPING = new DateMapping();
    public static final ByteArrayTypeMapping BYTE_ARRAY_MAPPING = new ByteArrayTypeMapping();

    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
        builder
            .typeInterceptor(BytesAnnotationTypeInterceptor::intercept)
            .typeInterceptor(PrimitiveTypeInterceptor::intercept)
            .typeInterceptor(BoxedPrimitiveTypeInterceptor::intercept)
            .typeInterceptor(
                (resolver, type, annotations) -> resolver.detectValueType(type))
            .typeInterceptor(RawInterceptor.of(byte[].class, type -> BYTE_ARRAY_MAPPING))
            .typeInterceptor(
                GenericInterceptor.of(List.class, 1, (type, m) -> new ListTypeMapping(type, m[0])))
            .typeInterceptor(GenericInterceptor.of(Map.class, 2,
                (type, m) -> new MapTypeMapping(type, m[0], m[1])))
            .typeInterceptor(GenericInterceptor.of(Map.class, 1,
                (type, m) -> new OptionalTypeMapping(type, m[0])))
            .typeInterceptor(RawInterceptor.of(String.class, type -> STRING_MAPPING))
            .typeInterceptor(RawInterceptor.of(Date.class, type -> DATE_MAPPING))
            .typeInterceptor(ObjectArrayInterceptor::intercept);

        builder
            .creatorMethodDetector(ConstructorPropertiesCreatorMethod::detect)
            .fieldReaderDetector(GetterFieldReader::detect)
            .bindingDetector(ConstructorEntityBinding::detect)
            .bindingDetector(BuilderEntityBinding::detect);

        return builder;
    }
}
