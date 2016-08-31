package eu.toolchain.scribe.typesafe;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EntityDecoder;
import eu.toolchain.scribe.StringEncoding;
import eu.toolchain.scribe.typesafe.encoding.MapDecoder;
import lombok.Data;

@Data
public class TypeSafeEncoding<T> implements StringEncoding<T> {
  private final Encoder<ConfigValue, T> parentEncoder;
  private final Decoder<ConfigValue, T> parentDecoder;

  public boolean isEntity() {
    return parentDecoder instanceof EntityDecoder || parentDecoder instanceof MapDecoder;
  }

  public ConfigValue encode(Context path, T instance) {
    return parentEncoder.encode(path, instance);
  }

  public Decoded<T> decode(Context path, ConfigValue instance) {
    return parentDecoder.decode(path, instance);
  }

  @Override
  public T decodeFromString(String input) {
    return decode(Context.ROOT, ConfigFactory.parseString(input).root()).orElseThrow(
        () -> new IllegalArgumentException("input decoded to nothing"));
  }

  @Override
  public String encodeAsString(T instance) {
    return encode(Context.ROOT, instance).render();
  }
}
