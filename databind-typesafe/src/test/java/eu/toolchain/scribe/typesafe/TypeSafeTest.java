package eu.toolchain.scribe.typesafe;

import eu.toolchain.scribe.AbstractDatabindTest;
import eu.toolchain.scribe.Scribe;
import eu.toolchain.scribe.NativeAnnotationsModule;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.TypeReference;
import org.junit.Ignore;
import org.junit.Test;

public class TypeSafeTest extends AbstractDatabindTest {
  private TypeSafeMapper mapper = new TypeSafeMapper(
      Scribe.defaultBuilder().install(new NativeAnnotationsModule()).build());

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

  @Test
  @Ignore
  @Override
  public void testEncodeNullMap() {
  }

  @Test
  @Ignore
  @Override
  public void testValue() throws Exception {
  }
}
