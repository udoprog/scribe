package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.MappingDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static eu.toolchain.scribe.TypeMatcher.any;
import static eu.toolchain.scribe.TypeMatcher.type;

@Data
public class OptionalMapping<T> implements Mapping {
  private final Mapping component;

  private final Function<T, Boolean> isPresent;
  private final Function<T, Object> get;
  private final Function<Object, T> of;
  private final Supplier<T> empty;

  public static <T> MappingDetector forType(
      final Class<T> optionalType, final Function<T, Boolean> isPresent,
      final Function<T, Object> get, final Function<Object, T> of, final Supplier<T> empty
  ) {
    final TypeMatcher matcher = type(optionalType, any());

    return (resolver, type) -> {
      if (matcher.matches(type)) {
        final Mapping component = type.getTypeParameter(0).map(resolver::mapping).get();
        return Stream
            .of(new OptionalMapping<>(component, isPresent, get, of, empty))
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
  public <Target, EntityTarget, Source> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory,
      final Flags flags
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

    return component.<Target, EntityTarget, Source>newEncoder(resolver, factory, flags).map(
        encoder);
  }

  @Override
  public <Target, Source> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
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

    return component.<Target, Source>newStreamEncoder(resolver, factory, flags).map(encoder);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, EntityTarget, Source> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return component.<Target, EntityTarget, Source>newDecoder(resolver, factory, flags).map(
        parent -> {
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
