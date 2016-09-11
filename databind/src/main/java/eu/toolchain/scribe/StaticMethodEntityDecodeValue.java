package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.DecodeValueDetector;
import eu.toolchain.scribe.detector.Match;
import eu.toolchain.scribe.detector.MatchPriority;
import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class StaticMethodEntityDecodeValue<Source> implements DecodeValue<Source> {
  private final JavaType sourceType;
  private final Mapping<Source> targetMapping;
  private final JavaType.Method method;

  @SuppressWarnings("unchecked")
  @Override
  public <Target, EntityTarget> Stream<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target, EntityTarget> factory,
      final Flags flags
  ) {
    return targetMapping
        .newDecoder(resolver, factory)
        .map(parent -> new StaticMethodEntityDecodeValueDecoder<>(parent, method));
  }

  public static DecodeValueDetector forAnnotation(
      final Class<? extends Annotation> annotation
  ) {
    return (resolver, sourceType, targetType) -> sourceType
        .findByAnnotation(JavaType::getMethods, annotation)
        .filter(m -> m.isPublic() && m.isStatic())
        .filter(m -> m
            .getParameters()
            .stream()
            .map(JavaType.Parameter::getParameterType)
            .collect(Collectors.toList())
            .equals(Collections.singletonList(targetType)))
        .flatMap(m -> {
          final Mapping<Object> targetMapping = resolver.mapping(targetType);
          return Stream.of(new StaticMethodEntityDecodeValue<>(sourceType, targetMapping, m));
        })
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
