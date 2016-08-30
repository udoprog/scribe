package eu.toolchain.scribe;

import lombok.Data;

import java.util.Map;

@Data
public class AbstractEntityDecoder<Target> implements EntityDecoder<Target, Object> {
  final Map<String, EntityDecoder<Target, Object>> byName;
  final DecoderFactory<Target> factory;
  final TypeEntityFieldDecoder<Target> typeDecoder;

  @Override
  public Decoded<Object> decode(
      final EntityFieldsDecoder<Target> decoder, final Context path
  ) {
    final String type = decoder
        .decodeField(typeDecoder, path.push(typeDecoder.getName()))
        .orElseThrow(() -> new RuntimeException("No type information available"));

    final EntityDecoder<Target, Object> sub = byName.get(type);

    if (sub == null) {
      throw new RuntimeException("Sub-type (" + type + ") required, but no such type available");
    }

    return sub.decode(decoder, path);
  }

  @Override
  public Decoded<Object> decode(final Context path, final Target instance) {
    return factory.newEntityDecoder(instance).flatMap(d -> decode(d, path));
  }
}
