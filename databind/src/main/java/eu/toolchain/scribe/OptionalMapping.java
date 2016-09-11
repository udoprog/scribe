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
public class OptionalMapping<OptionalType, Source> implements Mapping<OptionalType> {
  private final Mapping<Source> component;

  private final Function<OptionalType, Boolean> isPresent;
  private final Function<OptionalType, Source> get;
  private final Function<Source, OptionalType> of;
  private final Supplier<OptionalType> empty;

  @SuppressWarnings("unchecked")
  public static <OptionalType, Source> MappingDetector forType(
      final Class<OptionalType> optionalType, final Function<OptionalType, Boolean> isPresent,
      final Function<OptionalType, Source> get, final Function<Source, OptionalType> of,
      final Supplier<OptionalType> empty
  ) {
    final TypeMatcher matcher = type(optionalType, any());

    return (resolver, type) -> {
      if (!matcher.matches(type)) {
        return Stream.empty();
      }

      final Mapping<Source> component =
          (Mapping<Source>) type.getTypeParameter(0).map(resolver::mapping).get();

      final OptionalMapping<OptionalType, Source> m =
          new OptionalMapping<>(component, isPresent, get, of, empty);

      /* detector is expected to return an anonymous mapping */
      return Stream.of((Mapping<Object>) m).map(Match.withPriority(MatchPriority.HIGH));
    };
  }

  @Override
  public JavaType getType() {
    return component.getType();
  }

  @Override
  public <Target, EntityTarget> Stream<Encoder<Target, OptionalType>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    final Function<Encoder<Target, Source>, OptionalEncoder<Target>> encoder;

    if (resolver.isOptionPresent(DatabindOptions.OPTIONAL_EMPTY_AS_NULL)) {
      encoder = p -> new OptionalEncoder<Target>(p) {
        @Override
        public void encodeOptionally(
            final Context path, final OptionalType instance, final Consumer<Target> callback
        ) {
          callback.accept(encode(path, instance));
        }
      };
    } else {
      encoder = p -> new OptionalEncoder<Target>(p) {
        @SuppressWarnings("unchecked")
        @Override
        public void encodeOptionally(
            final Context path, final OptionalType instance, final Consumer<Target> callback
        ) {
          if (isPresent.apply(instance)) {
            callback.accept(parent.encode(path, get.apply(instance)));
          }
        }
      };
    }

    return component.newEncoder(resolver, factory, flags).map(encoder);
  }

  @Override
  public <Target> Stream<StreamEncoder<Target, OptionalType>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    final Function<StreamEncoder<Target, Source>, OptionalStreamEncoder<Target>> encoder;

    if (resolver.isOptionPresent(DatabindOptions.OPTIONAL_EMPTY_AS_NULL)) {
      encoder = p -> new OptionalStreamEncoder<Target>(p) {
        @Override
        public void streamEncodeOptionally(
            final Context path, final OptionalType instance, final Target target,
            final Consumer<Runnable> callback
        ) {
          callback.accept(() -> streamEncode(path, instance, target));
        }
      };
    } else {
      encoder = p -> new OptionalStreamEncoder<Target>(p) {
        @SuppressWarnings("unchecked")
        @Override
        public void streamEncodeOptionally(
            final Context path, final OptionalType instance, final Target target,
            final Consumer<Runnable> callback
        ) {
          if (isPresent.apply(instance)) {
            callback.accept(() -> parent.streamEncode(path, get.apply(instance), target));
          }
        }
      };
    }

    return component.newStreamEncoder(resolver, factory, flags).map(encoder);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, EntityTarget> Stream<Decoder<Target, OptionalType>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return component.newDecoder(resolver, factory, flags).map(OptionalDecoder::new);
  }

  @RequiredArgsConstructor
  abstract class OptionalEncoder<Target> implements Encoder<Target, OptionalType> {
    protected final Encoder<Target, Source> parent;

    @SuppressWarnings("unchecked")
    @Override
    public Target encode(final Context path, final OptionalType instance) {
      if (isPresent.apply(instance)) {
        return parent.encode(path, get.apply(instance));
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
  abstract class OptionalStreamEncoder<Target> implements StreamEncoder<Target, OptionalType> {
    protected final StreamEncoder<Target, Source> parent;

    @SuppressWarnings("unchecked")
    @Override
    public void streamEncode(
        final Context path, final OptionalType instance, final Target target
    ) {
      if (isPresent.apply(instance)) {
        parent.streamEncode(path, get.apply(instance), target);
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
  class OptionalDecoder<Target> implements Decoder<Target, OptionalType> {
    private final Decoder<Target, Source> parent;

    @Override
    public Decoded<OptionalType> decode(final Context path, final Target instance) {
      return parent.decode(path, instance).handle(of, empty);
    }

    @Override
    public Decoded<OptionalType> decodeOptionally(
        final Context path, final Decoded<Target> instance
    ) {
      return instance.flatMap(v -> parent.decode(path, v).map(of)).handleAbsent(empty);
    }
  }
}
