package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A type mapping for an alias.
 *
 * @param <From> Type that we are aliasing from.
 * @param <To> Type that we are aliasing to.
 * @see eu.toolchain.scribe.EntityMapperBuilder
 * @see eu.toolchain.scribe.detector.TypeAliasDetector
 */
@Data
public class TypeAliasMapping<From, To> implements Mapping {
  private final JavaType to;
  private final Mapping mapping;

  private final Function<To, From> convertTo;
  private final Function<From, To> convertFrom;

  @Override
  public JavaType getType() {
    return to;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory, Flags flags
  ) {
    return mapping.<Target, From>newEncoder(resolver, factory, flags).map(parent -> {
      final Encoder<Target, To> encoder = new Encoder<Target, To>() {
        @Override
        public Target encode(final Context path, final To instance) {
          return parent.encode(path, convertTo.apply(instance));
        }

        @Override
        public Target encodeEmpty(final Context path) {
          return parent.encodeEmpty(path);
        }
      };

      return (Encoder<Target, Source>) encoder;
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return mapping.<Target, From>newStreamEncoder(resolver, factory, flags).map(parent -> {
      final StreamEncoder<Target, To> encoder = new StreamEncoder<Target, To>() {
        @Override
        public void streamEncode(
            final Context path, final To instance, final Target target
        ) {
          parent.streamEncode(path, convertTo.apply(instance), target);
        }

        @Override
        public void streamEncodeEmpty(final Context path, final Target target) {
          parent.streamEncodeEmpty(path, target);
        }
      };

      return (StreamEncoder<Target, Source>) encoder;
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory, final Flags flags
  ) {
    return mapping.<Target, From>newDecoder(resolver, factory, flags).map(parent -> {
      final Decoder<Target, To> decoder =
          (path, instance) -> parent.decode(path, instance).map(convertFrom);

      return (Decoder<Target, Source>) decoder;
    });
  }
}
