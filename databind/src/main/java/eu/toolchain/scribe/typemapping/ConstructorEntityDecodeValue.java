package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.AccessibleType;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.JavaType;
import eu.toolchain.scribe.Match;
import eu.toolchain.scribe.MatchPriority;
import eu.toolchain.scribe.entitymapper.DecodeValueDetector;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ConstructorEntityDecodeValue implements DecodeValue {
  private final JavaType entityType;
  private final TypeMapping source;
  private final JavaType.Constructor constructor;

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    return source.<Target, Object>newDecoder(resolver, factory).map(
        parent -> (Decoder<Target, Source>) (path, instance) -> parent
            .decode(path, instance)
            .map(value -> {
              try {
                return (Source) constructor.newInstance(value);
              } catch (Exception e) {
                throw path.error("failed to get value", e);
              }
            }));
  }

  public static DecodeValueDetector forAnnotation(
      final Class<? extends Annotation> annotation
  ) {
    return (resolver, entityType, targetType) -> entityType
        .findByAnnotation(JavaType::getConstructors, annotation)
        .filter(AccessibleType::isPublic)
        .filter(m -> m
            .getParameters()
            .stream()
            .map(JavaType.Parameter::getParameterType)
            .collect(Collectors.toList())
            .equals(Collections.singletonList(targetType)))
        .flatMap(c -> {
          final TypeMapping source = resolver.mapping(targetType);
          return Stream.of(new ConstructorEntityDecodeValue(entityType, source, c));
        })
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
