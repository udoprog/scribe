package eu.toolchain.ogt.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import eu.toolchain.ogt.AbstractDatabindTest;
import eu.toolchain.ogt.EntityMapper;
import eu.toolchain.ogt.JacksonAnnotationsModule;
import eu.toolchain.ogt.StringEncoding;
import eu.toolchain.ogt.TypeReference;

public class JacksonTest extends AbstractDatabindTest<JsonNode> {
    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private JacksonEntityMapper mapper = new JacksonEntityMapper(
        EntityMapper.defaultBuilder().register(new JacksonAnnotationsModule()).build(),
        JSON_FACTORY);

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
