package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.DecodeValueDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.AccessibleType;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ConstructorEntityDecodeValue implements DecodeValue {
  private final JavaType sourceType;
  private final Mapping targetMapping;
  private final JavaType.Constructor constructor;

  @SuppressWarnings("unchecked")
  @Override
  public <Target, EntityTarget, Source> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return targetMapping.<Target, EntityTarget, Source>newDecoder(resolver, factory).map(
        parent -> new ConstructorEntityDecodeValueDecoder<>(constructor, parent));
  }

  public static DecodeValueDetector forAnnotation(final Class<? extends Annotation> annotation) {
    return (resolver, sourceType, targetType) -> sourceType
        .findByAnnotation(JavaType::getConstructors, annotation)
        .filter(AccessibleType::isPublic)
        .filter(m -> m
            .getParameters()
            .stream()
            .map(JavaType.Parameter::getParameterType)
            .collect(Collectors.toList())
            .equals(Collections.singletonList(targetType)))
        .flatMap(c -> {
          final Mapping targetMapping = resolver.mapping(targetType);
          return Stream.of(new ConstructorEntityDecodeValue(sourceType, targetMapping, c));
        })
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
