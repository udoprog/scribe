package eu.toolchain.scribe;

import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class EncodedTypeMapping implements TypeMapping {
  private final JavaType type;

  @Override
  public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    final List<Encoder<Target, Source>> results =
        factory.<Source>newEncoder(resolver, type).collect(Collectors.toList());

    if (results.size() > 1) {
      throw new IllegalArgumentException(
          "Type (" + type + ") has more than one matching encoder: " + results);
    }

    return results.stream().findFirst();
  }

  @Override
  public <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    final List<StreamEncoder<Target, Source>> results =
        factory.<Source>newStreamEncoder(resolver, type).collect(Collectors.toList());

    if (results.size() > 1) {
      throw new IllegalArgumentException(
          "Type (" + type + ") has more than one matching encoder: " + results);
    }

    return results.stream().findFirst();
  }

  @Override
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    final List<Decoder<Target, Source>> results =
        factory.<Source>newDecoder(resolver, type).collect(Collectors.toList());

    if (results.size() > 1) {
      throw new IllegalArgumentException(
          "Type (" + type + ") has more than one matching decoder: " + results);
    }

    return results.stream().findFirst();
  }
}
