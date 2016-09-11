package eu.toolchain.scribe;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.toolchain.scribe.SimpleTypeAlias.simpleAlias;
import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.isArray;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;
import static eu.toolchain.scribe.detector.MappingDetector.matchMapping;
import static eu.toolchain.scribe.detector.TypeAliasDetector.matchAlias;

public class DefaultModule implements Module {
  private static final Base64.Encoder b64e = Base64.getEncoder();
  private static final Base64.Decoder b64d = Base64.getDecoder();

  @Override
  public void register(ScribeBuilder b) {
    /* support constructors */
    b
        .instanceBuilder(ConstructorInstanceBuilder.forEmpty())
        .instanceBuilder(ConstructorInstanceBuilder.forAnnotation(ConstructorProperties.class,
            a -> Optional.of(Arrays.asList(a.value()))));

    b.fieldReader(GetterFieldReader::detect);

    b.classEncoding(MethodClassEncoding::detect);
    b.classEncoding(BuilderClassEncoding::detect);

    b.mapping(
        OptionalMapping.forType(Optional.class, Optional::isPresent, Optional::get, Optional::of,
            Optional::empty));

    b.mapping(matchMapping(type(String.class), EncodedMapping::new));

    b.mapping(matchMapping(isArray(), EncodedMapping::new));
    b.mapping(matchMapping(isPrimitive(), EncodedMapping::new));

    b.mapping(matchMapping(type(List.class, any()), EncodedMapping::new));
    b.mapping(matchMapping(type(Map.class, any(), any()), EncodedMapping::new));

    b.typeAlias(matchAlias(isPrimitive(char.class),
        type -> simpleAlias(Character.class, String.class, c -> new String(new char[]{c}),
            s -> s.charAt(0))));

    b.typeAlias(matchAlias(isPrimitive(byte.class),
        type -> simpleAlias(Byte.class, String.class, c -> b64e.encodeToString(new byte[]{c}),
            s -> b64d.decode(s)[0])));

    b.typeAlias(matchAlias(type(byte[].class),
        type -> simpleAlias(byte[].class, String.class, b64e::encodeToString, b64d::decode)));
  }
}
