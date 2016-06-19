package eu.toolchain.ogt;

import eu.toolchain.ogt.creatormethod.ConstructorPropertiesCreatorMethod;
import eu.toolchain.ogt.entitybinding.BuilderEntityBinding;
import eu.toolchain.ogt.entitybinding.ConstructorEntityBinding;
import eu.toolchain.ogt.fieldreader.GetterFieldReader;
import eu.toolchain.ogt.typemapping.OptionalTypeMapping;

import java.util.List;
import java.util.Map;

import static eu.toolchain.ogt.TypeMatcher.any;
import static eu.toolchain.ogt.TypeMatcher.anyOf;
import static eu.toolchain.ogt.TypeMatcher.exact;
import static eu.toolchain.ogt.TypeMatcher.isArray;
import static eu.toolchain.ogt.TypeMatcher.isPrimitive;
import static eu.toolchain.ogt.TypeMatcher.parameterized;
import static eu.toolchain.ogt.typemapper.TypeMapper.match;

public class DefaultModule implements Module {
    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> b) {
        b
            .creatorMethodDetector(ConstructorPropertiesCreatorMethod::detect)
            .fieldReaderDetector(GetterFieldReader::detect)
            .bindingDetector(ConstructorEntityBinding::detect)
            .bindingDetector(BuilderEntityBinding::detect);

        b.typeMapper(OptionalTypeMapping::detect);

        b.typeMapper(match(exact(String.class), EncodedTypeMapping::new));

        b.typeMapper(match(anyOf(isArray(), isPrimitive()), EncodedTypeMapping::new));
        b.typeMapper(match(parameterized(List.class, any()), EncodedTypeMapping::new));
        b.typeMapper(match(parameterized(Map.class, any(), any()), EncodedTypeMapping::new));

        return b;
    }
}
