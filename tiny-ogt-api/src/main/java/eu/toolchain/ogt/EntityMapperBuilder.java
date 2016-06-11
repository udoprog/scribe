package eu.toolchain.ogt;

import eu.toolchain.ogt.entitymapper.BindingDetector;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import eu.toolchain.ogt.entitymapper.FieldNameDetector;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.entitymapper.TypeInterceptor;
import eu.toolchain.ogt.entitymapper.TypeNameDetector;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;

public interface EntityMapperBuilder<T> {
    EntityMapperBuilder<T> fieldReaderDetector(FieldReaderDetector fieldReader);

    EntityMapperBuilder<T> creatorMethodDetector(CreatorMethodDetector creatorMethod);

    EntityMapperBuilder<T> bindingDetector(BindingDetector binding);

    EntityMapperBuilder<T> subTypesDetector(SubTypesDetector subTypeDetector);

    EntityMapperBuilder<T> valueTypeDetector(ValueTypeDetector valueTypeDetector);

    EntityMapperBuilder<T> fieldNameDetector(FieldNameDetector fieldNameDetector);

    EntityMapperBuilder<T> typeNameDetector(TypeNameDetector typeNameDetector);

    EntityMapperBuilder<T> typeInterceptor(TypeInterceptor typeInterceptor);

    EntityMapperBuilder<T> register(Module module);

    T build();
}
