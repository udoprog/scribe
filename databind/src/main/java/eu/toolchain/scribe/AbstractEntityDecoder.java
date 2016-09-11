package eu.toolchain.scribe;

import lombok.Data;

import java.util.Map;

@Data
public class AbstractEntityDecoder<Target, EntityTarget, Source>
    implements EntityDecoder<Target, EntityTarget, Source> {
  final Map<String, EntityDecoder<Target, EntityTarget, Source>> byName;
  final DecoderFactory<Target, EntityTarget> factory;
  final TypeEntityFieldDecoder<Target> typeDecoder;

  @Override
  public Source decodeEntity(final Context path, final EntityTarget entity) {
    final EntityFieldsDecoder<Target> decoder = factory.newEntityDecoder(entity);
    return decodeEntity(path, entity, decoder);
  }

  @Override
  public Source decodeEntity(
      final Context path, final EntityTarget entity, final EntityFieldsDecoder<Target> decoder
  ) {
    final String type = decoder
        .decodeField(typeDecoder, path.push(typeDecoder.getName()))
        .orElseThrow(() -> path.error("No type information available"));

    final EntityDecoder<Target, EntityTarget, Source> sub = byName.get(type);

    if (sub == null) {
      throw path.error("Sub-type (" + type + ") required, but no such type available");
    }

    return sub.decodeEntity(path, entity, decoder);
  }

  @Override
  public Decoded<Source> decode(final Context path, final Target instance) {
    return factory.valueAsEntity(instance).map(i -> decodeEntity(path, i));
  }
}
