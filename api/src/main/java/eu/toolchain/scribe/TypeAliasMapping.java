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
 * @see ScribeBuilder
 * @see eu.toolchain.scribe.detector.TypeAliasDetector
 */
@Data
public class TypeAliasMapping<From, To> implements Mapping<To> {
  private final JavaType to;
  private final Mapping<From> mapping;

  private final Function<To, From> convertTo;
  private final Function<From, To> convertFrom;

  @Override
  public JavaType getType() {
    return to;
  }

  @Override
  public <Target, EntityTarget> Stream<Encoder<Target, To>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory, Flags flags
  ) {
    return mapping.newEncoder(resolver, factory, flags).map(parent -> {
      return new Encoder<Target, To>() {
        @Override
        public Target encode(final Context path, final To instance) {
          return parent.encode(path, convertTo.apply(instance));
        }

        @Override
        public Target encodeEmpty(final Context path) {
          return parent.encodeEmpty(path);
        }
      };
    });
  }

  @Override
  public <Target> Stream<StreamEncoder<Target, To>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return mapping.newStreamEncoder(resolver, factory, flags).map(parent -> {
      return new StreamEncoder<Target, To>() {
        @Override
        public void streamEncode(final Context path, final To instance, final Target target) {
          parent.streamEncode(path, convertTo.apply(instance), target);
        }

        @Override
        public void streamEncodeEmpty(final Context path, final Target target) {
          parent.streamEncodeEmpty(path, target);
        }
      };
    });
  }

  @Override
  public <Target, EntityTarget> Stream<Decoder<Target, To>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return mapping.newDecoder(resolver, factory, flags).map(parent -> {
      return (path, instance) -> parent.decode(path, instance).map(convertFrom);
    });
  }
}
