package eu.toolchain.ogt;

import eu.toolchain.ogt.entitymapper.BindingDetector;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import eu.toolchain.ogt.entitymapper.NameDetector;
import eu.toolchain.ogt.entitymapper.PropertyNameDetector;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.entitymapper.TypeInterceptor;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;

public interface EntityMapperBuilder<T> {
    EntityMapperBuilder<T> registerFieldReader(FieldReaderDetector fieldReader);

    EntityMapperBuilder<T> registerCreatorMethod(CreatorMethodDetector creatorMethod);

    EntityMapperBuilder<T> registerBinding(BindingDetector binding);

    EntityMapperBuilder<T> registerSubTypes(SubTypesDetector subTypeDetector);

    EntityMapperBuilder<T> registerValueType(ValueTypeDetector valueTypeDetector);

    EntityMapperBuilder<T> registerPropertyNameDetector(
        PropertyNameDetector propertyNameDetector
    );

    EntityMapperBuilder<T> registerNameDetector(NameDetector nameDetector);

    EntityMapperBuilder<T> registerTypeInterceptor(TypeInterceptor typeInterceptor);

    EntityMapperBuilder<T> register(Module module);

    T build();
}
