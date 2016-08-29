package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.DatabindOptions;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.TypeMatcher;
import eu.toolchain.scribe.typemapper.TypeMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.type;

@Data
public class OptionalTypeMapping<T> implements TypeMapping {
  private final TypeMapping component;

  private final Function<T, Boolean> isPresent;
  private final Function<T, Object> get;
  private final Function<Object, T> of;
  private final Supplier<T> empty;

  public static <T> TypeMapper forType(
      final Class<T> optionalType, final Function<T, Boolean> isPresent,
      final Function<T, Object> get, final Function<Object, T> of, final Supplier<T> empty
  ) {
    final TypeMatcher matcher = type(optionalType, any());

    return (resolver, type) -> {
      if (matcher.matches(type)) {
        final TypeMapping component = type.getTypeParameter(0).map(resolver::mapping).get();
        return Stream
            .of(new OptionalTypeMapping<>(component, isPresent, get, of, empty))
            .map(Match.withPriority(MatchPriority.HIGH));
      }

      return Stream.empty();
    };
  }

  @Override
  public JavaType getType() {
    return component.getType();
  }

  @Override
  public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final Flags flags, final EncoderFactory<Target> factory
  ) {
    final Function<Encoder<Target, Source>, OptionalEncoder<Target, Source>> encoder;

    if (resolver.isOptionPresent(DatabindOptions.OPTIONAL_EMPTY_AS_NULL)) {
      encoder = p -> new OptionalEncoder<Target, Source>(p) {
        @Override
        public void encodeOptionally(
            final Context path, final Source instance, final Consumer<Target> callback
        ) {
          callback.accept(encode(path, instance));
        }
      };
    } else {
      encoder = p -> new OptionalEncoder<Target, Source>(p) {
        @SuppressWarnings("unchecked")
        @Override
        public void encodeOptionally(
            final Context path, final Source instance, final Consumer<Target> callback
        ) {
          final T o = (T) instance;

          if (isPresent.apply(o)) {
            callback.accept(parent.encode(path, (Source) get.apply(o)));
          }
        }
      };
    }

    return component.<Target, Source>newEncoder(resolver, flags, factory).map(encoder);
  }

  @Override
  public <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final Flags flags, final StreamEncoderFactory<Target> factory
  ) {
    final Function<StreamEncoder<Target, Source>, OptionalStreamEncoder<Target, Source>> encoder;

    if (resolver.isOptionPresent(DatabindOptions.OPTIONAL_EMPTY_AS_NULL)) {
      encoder = p -> new OptionalStreamEncoder<Target, Source>(p) {
        @Override
        public void streamEncodeOptionally(
            final Context path, final Source instance, final Target target,
            final Consumer<Runnable> callback
        ) {
          callback.accept(() -> {
            streamEncode(path, instance, target);
          });
        }
      };
    } else {
      encoder = p -> new OptionalStreamEncoder<Target, Source>(p) {
        @SuppressWarnings("unchecked")
        @Override
        public void streamEncodeOptionally(
            final Context path, final Source instance, final Target target,
            final Consumer<Runnable> callback
        ) {
          final T o = (T) instance;

          if (isPresent.apply(o)) {
            callback.accept(() -> parent.streamEncode(path, (Source) get.apply(o), target));
          }
        }
      };
    }

    return component.<Target, Source>newStreamEncoder(resolver, flags, factory).map(encoder);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final Flags flags, final DecoderFactory<Target> factory
  ) {
    return component.<Target, Source>newDecoder(resolver, flags, factory).map(parent -> {
      return (Decoder<Target, Source>) new OptionalDecoder<>(parent);
    });
  }

  @RequiredArgsConstructor
  abstract class OptionalEncoder<Target, Source> implements Encoder<Target, Source> {
    protected final Encoder<Target, Source> parent;

    @SuppressWarnings("unchecked")
    @Override
    public Target encode(final Context path, final Source instance) {
      final T o = (T) instance;

      if (isPresent.apply(o)) {
        return parent.encode(path, (Source) get.apply(o));
      } else {
        return parent.encodeEmpty(path);
      }
    }

    @Override
    public Target encodeEmpty(final Context path) {
      return parent.encodeEmpty(path);
    }
  }

  @RequiredArgsConstructor
  abstract class OptionalStreamEncoder<Target, Source> implements StreamEncoder<Target, Source> {
    protected final StreamEncoder<Target, Source> parent;

    @SuppressWarnings("unchecked")
    @Override
    public void streamEncode(
        final Context path, final Source instance, final Target target
    ) {
      final T o = (T) instance;

      if (isPresent.apply(o)) {
        parent.streamEncode(path, (Source) get.apply(o), target);
      } else {
        parent.streamEncodeEmpty(path, target);
      }
    }

    @Override
    public void streamEncodeEmpty(final Context path, final Target target) {
      parent.streamEncodeEmpty(path, target);
    }
  }

  @Data
  class OptionalDecoder<Target, Source> implements Decoder<Target, T> {
    private final Decoder<Target, Source> parent;

    @Override
    public Decoded<T> decode(final Context path, final Target instance) {
      return parent.decode(path, instance).handle(of, empty);
    }

    @Override
    public Decoded<T> decodeOptionally(final Context path, final Decoded<Target> instance) {
      return instance.flatMap(v -> parent.decode(path, v).map(of)).handleAbsent(empty);
    }
  }
}
