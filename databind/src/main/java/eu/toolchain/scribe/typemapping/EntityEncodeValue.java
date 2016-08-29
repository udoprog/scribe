package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Encoder;
import eu.toolchain.scribe.EncoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.StreamEncoder;
import eu.toolchain.scribe.StreamEncoderFactory;
import eu.toolchain.scribe.entitymapper.EncodeValueDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class EntityEncodeValue implements EncodeValue {
  private final JavaType entityType;
  private final TypeMapping targetMapping;
  private final JavaType.Method valueMethod;

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target> factory
  ) {
    return targetMapping.<Target, Source>newEncoder(resolver, Flags.empty(), factory).map(
        parent -> new Encoder<Target, Source>() {
          @Override
          public Target encode(final Context path, final Source instance) {
            final Source value;

            try {
              value = (Source) valueMethod.invoke(instance);
            } catch (Exception e) {
              throw path.error("failed to get value", e);
            }

            return parent.encode(path, value);
          }

          @Override
          public Target encodeEmpty(final Context path) {
            return parent.encodeEmpty(path);
          }
        });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Optional<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory
  ) {
    return targetMapping.<Target, Source>newStreamEncoder(resolver, Flags.empty(), factory).map(
        parent -> new StreamEncoder<Target, Source>() {
          @Override
          public void streamEncode(
              final Context path, final Source instance, final Target target
          ) {
            final Source value;

            try {
              value = (Source) valueMethod.invoke(instance);
            } catch (Exception e) {
              throw path.error("failed to get value", e);
            }

            parent.streamEncode(path, value, target);
          }

          @Override
          public void streamEncodeEmpty(final Context path, final Target target) {
            parent.streamEncodeEmpty(path, target);
          }
        });
  }

  public static EncodeValueDetector forAnnotation(
      final Class<? extends Annotation> annotation
  ) {
    return (resolver, sourceType) -> sourceType
        .findByAnnotation(JavaType::getMethods, annotation)
        .filter(m -> m.isPublic() && !m.isStatic())
        .flatMap(m -> {
          if (m.getParameters().size() != 0) {
            throw new IllegalArgumentException(
                String.format("@%s method must have no parameters: %s", annotation.getSimpleName(),
                    m));
          }

          final TypeMapping targetType = resolver.mapping(m.getReturnType());
          return Stream.of(new EntityEncodeValue(sourceType, targetType, m));
        })
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
