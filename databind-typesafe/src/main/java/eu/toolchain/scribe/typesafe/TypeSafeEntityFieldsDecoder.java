package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.EntityFieldDecoder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class TypeSafeEntityFieldsDecoder implements EntityFieldsDecoder<ConfigValue> {
  private final Map<String, ConfigValue> value;

  @Override
  public <Source> Decoded<Source> decodeField(
      final EntityFieldDecoder<ConfigValue, Source> decoder, final Context path
  ) {
    return decoder.decodeOptionally(path, Decoded.ofNullable(value.get(decoder.getName())));
  }
}
