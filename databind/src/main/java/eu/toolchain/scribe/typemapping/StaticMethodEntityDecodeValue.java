package eu.toolchain.scribe.typemapping;

import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.Decoder;
import eu.toolchain.scribe.DecoderFactory;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Flags;
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
public class StaticMethodEntityDecodeValue implements DecodeValue {
  private final JavaType entityType;
  private final TypeMapping target;
  private final JavaType.Method method;

  @SuppressWarnings("unchecked")
  @Override
  public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
      final EntityResolver resolver, final DecoderFactory<Target> factory
  ) {
    return target
        .newDecoder(resolver, Flags.empty(), factory)
        .map(parent -> (Decoder<Target, Source>) (path, instance) -> {
          final Object value = parent.decode(path, instance);

          try {
            return Decoded.of((Source) method.invoke(null, value));
          } catch (Exception e) {
            throw path.error("failed to get value", e);
          }
        });
  }

  public static DecodeValueDetector forAnnotation(
      final Class<? extends Annotation> annotation
  ) {
    return (resolver, entityType, targetType) -> entityType
        .findByAnnotation(JavaType::getMethods, annotation)
        .filter(m -> m.isPublic() && m.isStatic())
        .filter(m -> m
            .getParameters()
            .stream()
            .map(JavaType.Parameter::getParameterType)
            .collect(Collectors.toList())
            .equals(Collections.singletonList(targetType)))
        .flatMap(m -> {
          final TypeMapping source = resolver.mapping(targetType);
          return Stream.of(new StaticMethodEntityDecodeValue(entityType, source, m));
        })
        .map(Match.withPriority(MatchPriority.HIGH));
  }
}
