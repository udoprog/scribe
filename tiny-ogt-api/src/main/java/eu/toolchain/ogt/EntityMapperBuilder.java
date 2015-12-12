package eu.toolchain.ogt;

import eu.toolchain.ogt.entitymapper.BindingDetector;
import eu.toolchain.ogt.entitymapper.CreatorMethodDetector;
import eu.toolchain.ogt.entitymapper.FieldReaderDetector;
import eu.toolchain.ogt.entitymapper.NameDetector;
import eu.toolchain.ogt.entitymapper.PropertyNameDetector;
import eu.toolchain.ogt.entitymapper.SubTypesDetector;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;

public interface EntityMapperBuilder<T> {
    public EntityMapperBuilder<T> registerFieldReader(FieldReaderDetector fieldReader);

    public EntityMapperBuilder<T> registerCreatorMethod(CreatorMethodDetector creatorMethod);

    public EntityMapperBuilder<T> registerBinding(BindingDetector binding);

    public EntityMapperBuilder<T> registerSubTypes(SubTypesDetector subTypeDetector);

    public EntityMapperBuilder<T> registerValueType(ValueTypeDetector valueTypeDetector);

    public EntityMapperBuilder<T> registerPropertyNameDetector(
            PropertyNameDetector propertyNameDetector);

    public EntityMapperBuilder<T> registerNameDetector(NameDetector nameDetector);

    public EntityMapperBuilder<T> register(Module module);

    public T build();
}
