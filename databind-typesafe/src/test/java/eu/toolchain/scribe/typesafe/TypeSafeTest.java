package eu.toolchain.scribe.typesafe;

import eu.toolchain.scribe.AbstractDatabindTest;
import eu.toolchain.scribe.EntityMapper;
import eu.toolchain.scribe.NativeAnnotationsModule;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.TypeReference;
import org.junit.internal.AssumptionViolatedException;

public class TypeSafeTest extends AbstractDatabindTest {
  private TypeSafeEntityMapper mapper = new TypeSafeEntityMapper(
      EntityMapper.defaultBuilder().install(new NativeAnnotationsModule()).build());

  @Override
  protected <S> StringEncoding<S> encodingFor(
      final TypeReference<S> type, final Option... options
  ) {
    return mapper.withOptions(options).encodingFor(type);
  }

  @Override
  protected <S> StringEncoding<S> encodingFor(final Class<S> type, final Option... options) {
    final TypeSafeEncoding<S> encoding = mapper.withOptions(options).encodingFor(type);

    if (!encoding.isEntity()) {
      throw new AssumptionViolatedException(
          "typesafe only supports encoding/decoding to and from objects");
    }

    return encoding;
  }
}
