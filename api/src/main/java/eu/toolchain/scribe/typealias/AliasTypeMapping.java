package eu.toolchain.scribe.typealias;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.typemapping.TypeMapping;
import lombok.Data;

import java.util.Optional;
import java.util.function.Function;

@Data
public class AliasTypeMapping<From, To> implements TypeMapping {
  private final JavaType to;
  private final TypeMapping mapping;

  private final Function<To, From> convertTo;
  private final Function<From, To> convertFrom;

  @Override
  public JavaType getType() {
    return to;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, Flags flags, final EncoderFactory<Target> factory
  ) {
    return mapping.<Target, From>newEncoder(resolver, flags, factory).map(parent -> {
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
  public <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final Flags flags, final StreamEncoderFactory<Target> factory
  ) {
    return mapping.<Target, From>newStreamEncoder(resolver, flags, factory).map(parent -> {
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
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final Flags flags, final DecoderFactory<Target> factory
  ) {
    return mapping.<Target, From>newDecoder(resolver, flags, factory).map(parent -> {
      final Decoder<Target, To> decoder =
          (path, instance) -> parent.decode(path, instance).map(convertFrom);

      return (Decoder<Target, Source>) decoder;
    });
  }
}
