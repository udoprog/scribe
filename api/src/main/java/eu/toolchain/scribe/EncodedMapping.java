package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class EncodedMapping implements Mapping {
  private final JavaType type;

  @Override
  public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final Flags flags, final EncoderFactory<Target> factory
  ) {
    final List<Encoder<Target, Source>> results =
        factory.<Source>newEncoder(resolver, flags, type).collect(Collectors.toList());

    if (results.size() > 1) {
      throw new IllegalArgumentException(
          "Type (" + type + ") has more than one matching encoder: " + results);
    }

    return results.stream().findFirst();
  }

  @Override
  public <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final Flags flags, final StreamEncoderFactory<Target> factory
  ) {
    final List<StreamEncoder<Target, Source>> results =
        factory.<Source>newStreamEncoder(resolver, flags, type).collect(Collectors.toList());

    if (results.size() > 1) {
      throw new IllegalArgumentException(
          "Type (" + type + ") has more than one matching encoder: " + results);
    }

    return results.stream().findFirst();
  }

  @Override
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final Flags flags, final DecoderFactory<Target> factory
  ) {
    final List<Decoder<Target, Source>> results =
        factory.<Source>newDecoder(resolver, flags, type).collect(Collectors.toList());

    if (results.size() > 1) {
      throw new IllegalArgumentException(
          "Type (" + type + ") has more than one matching decoder: " + results);
    }

    return results.stream().findFirst();
  }
}
