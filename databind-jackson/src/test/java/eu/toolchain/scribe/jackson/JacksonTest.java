package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import eu.toolchain.scribe.AbstractDatabindTest;
import eu.toolchain.scribe.EntityMapper;
import eu.toolchain.scribe.JacksonAnnotationsModule;
import eu.toolchain.scribe.NativeAnnotationsModule;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.TypeReference;

public class JacksonTest extends AbstractDatabindTest {
  private static final JsonFactory JSON_FACTORY = new JsonFactory();

  private JacksonEntityMapper mapper = new JacksonEntityMapper(EntityMapper
      .defaultBuilder()
      .register(new JacksonAnnotationsModule())
      .register(new NativeAnnotationsModule())
      .build(), JSON_FACTORY);

  @Override
  protected <S> StringEncoding<S> encodingFor(
      final TypeReference<S> type, final Option... options
  ) {
    return mapper.withOptions(options).encodingFor(type);
  }

  @Override
  protected <S> StringEncoding<S> encodingFor(final Class<S> type, final Option... options) {
    return mapper.withOptions(options).encodingFor(type);
  }
}
