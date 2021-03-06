package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.EncodeValueDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

@Data
public class EntityEncodeValue<Source> implements EncodeValue<Source> {
  private final JavaType sourceType;
  private final Mapping<Source> targetMapping;
  private final JavaType.Method valueMethod;

  @SuppressWarnings("unchecked")
  @Override
  public <Target, EntityTarget> Stream<Encoder<Target, Source>> newEncoder(
      final EntityResolver resolver, final EncoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return targetMapping.<Target, EntityTarget>newEncoder(resolver, factory).map(
        parent -> new EntityEncodeValueEncoder<>(valueMethod, parent));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <Target> Stream<StreamEncoder<Target, Source>> newStreamEncoder(
      final EntityResolver resolver, final StreamEncoderFactory<Target> factory, final Flags flags
  ) {
    return targetMapping
        .newStreamEncoder(resolver, factory)
        .map(parent -> new EntityEncodeValueStreamEncoder(valueMethod, parent));
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

          final Mapping<Object> targetMapping = resolver.mapping(m.getReturnType());
          return Stream.of(new EntityEncodeValue<>(sourceType, targetMapping, m));
        })
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
