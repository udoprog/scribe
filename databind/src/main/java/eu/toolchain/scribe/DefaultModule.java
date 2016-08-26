package eu.toolchain.scribe;

import eu.toolchain.scribe.creatormethod.ConstructorCreatorMethod;
import eu.toolchain.scribe.entitymapping.BuilderEntityMapping;
import eu.toolchain.scribe.entitymapping.DefaultEntityMapping;
import eu.toolchain.scribe.fieldreader.GetterFieldReader;
import eu.toolchain.scribe.typemapping.OptionalTypeMapping;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.isArray;
import static eu.toolchain.scribe.TypeMatcher.isPrimitive;
import static eu.toolchain.scribe.TypeMatcher.type;
import static eu.toolchain.scribe.entitymapper.TypeAliasDetector.matchAlias;
import static eu.toolchain.scribe.typealias.SimpleTypeAlias.simpleAlias;
import static eu.toolchain.scribe.typemapper.TypeMapper.matchMapper;

public class DefaultModule implements Module {
  private static final Base64.Encoder b64e = Base64.getEncoder();
  private static final Base64.Decoder b64d = Base64.getDecoder();

  @Override
  public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> b) {
        /* support empty constructors */
    b.creatorMethodDetector(ConstructorCreatorMethod.forEmpty());

    b
        .creatorMethodDetector(ConstructorCreatorMethod.forAnnotation(ConstructorProperties.class,
            a -> Optional.of(Arrays.asList(a.value()))))
        .fieldReaderDetector(GetterFieldReader::detect);

    b
        .entityMappingDetector(DefaultEntityMapping::detect)
        .entityMappingDetector(BuilderEntityMapping::detect);

    b.typeMapper(OptionalTypeMapping.forType(Optional.class, Optional::isPresent, Optional::get,
        Optional::of, Optional::empty));

    b.typeMapper(matchMapper(type(String.class), EncodedTypeMapping::new));

    b.typeMapper(matchMapper(isArray(), EncodedTypeMapping::new));
    b.typeMapper(matchMapper(isPrimitive(), EncodedTypeMapping::new));

    b.typeMapper(matchMapper(type(List.class, any()), EncodedTypeMapping::new));
    b.typeMapper(matchMapper(type(Map.class, any(), any()), EncodedTypeMapping::new));

    b.typeAliasDetector(matchAlias(isPrimitive(char.class),
        type -> simpleAlias(Character.class, String.class, c -> new String(new char[]{c}),
            s -> s.charAt(0))));

    b.typeAliasDetector(matchAlias(isPrimitive(byte.class),
        type -> simpleAlias(Byte.class, String.class, c -> b64e.encodeToString(new byte[]{c}),
            s -> b64d.decode(s)[0])));

    b.typeAliasDetector(matchAlias(type(byte[].class),
        type -> simpleAlias(byte[].class, String.class, b64e::encodeToString, b64d::decode)));

    return b;
  }
}
