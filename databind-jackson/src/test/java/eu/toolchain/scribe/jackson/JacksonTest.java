package eu.toolchain.scribe.jackson;

import eu.toolchain.scribe.AbstractDatabindTest;
import eu.toolchain.scribe.Scribe;
import eu.toolchain.scribe.JacksonAnnotationsModule;
import eu.toolchain.scribe.NativeAnnotationsModule;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.TypeReference;

public class JacksonTest extends AbstractDatabindTest {
  private JacksonMapper mapper = new JacksonMapper(Scribe
      .defaultBuilder()
      .install(new JacksonAnnotationsModule())
      .install(new NativeAnnotationsModule())
      .build());

  @Override
  protected <S> StringEncoding<S> encodingFor(
      final TypeReference<S> type, final Option... options
  ) {
    return mapper.withOptions(options).stringEncodingFor(type);
  }

  @Override
  protected <S> StringEncoding<S> encodingFor(final Class<S> type, final Option... options) {
    return mapper.withOptions(options).stringEncodingFor(type);
  }
}
