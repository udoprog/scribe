package eu.toolchain.ogt.typesafe;

import com.typesafe.config.ConfigValue;
import eu.toolchain.ogt.AbstractDatabindTest;
import eu.toolchain.ogt.EntityMapper;
import eu.toolchain.ogt.NativeAnnotationsModule;
import eu.toolchain.ogt.StringEncoding;
import eu.toolchain.ogt.TypeReference;

public class TypeSafeTest extends AbstractDatabindTest<ConfigValue> {
    private TypeSafeEntityMapper mapper = new TypeSafeEntityMapper(
        EntityMapper.defaultBuilder().register(new NativeAnnotationsModule()).build());

    @Override
    protected <S> StringEncoding<S> encodingFor(
        final TypeReference<S> type
    ) {
        return mapper.encodingFor(type);
    }

    @Override
    protected <S> StringEncoding<S> encodingFor(final Class<S> type) {
        return mapper.encodingFor(type);
    }
}
