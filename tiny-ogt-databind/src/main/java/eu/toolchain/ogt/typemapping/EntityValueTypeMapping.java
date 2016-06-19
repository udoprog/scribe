package eu.toolchain.ogt.typemapping;

import com.google.common.collect.ImmutableList;
import eu.toolchain.ogt.Decoder;
import eu.toolchain.ogt.Encoder;
import eu.toolchain.ogt.EncodingFactory;
import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.Match;
import eu.toolchain.ogt.Priority;
import eu.toolchain.ogt.creatormethod.CreatorMethod;
import eu.toolchain.ogt.creatormethod.InstanceBuilder;
import eu.toolchain.ogt.entitymapper.ValueTypeDetector;
import eu.toolchain.ogt.type.JavaType;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class EntityValueTypeMapping implements TypeMapping {
    private final JavaType sourceType;
    private final TypeMapping mapping;
    private final InstanceBuilder instanceBuilder;
    private final JavaType.Method mMethod;

    @Override
    public JavaType getType() {
        return sourceType;
    }

    @Override
    public <Target, Source> Optional<Encoder<Target, Source>> newEncoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        return mapping.<Target, Source>newEncoder(resolver, factory).map(
            parent -> (Encoder<Target, Source>) (path, instance) -> {
                final Source newInstance =
                    (Source) instanceBuilder.newInstance(ImmutableList.of(instance));
                return parent.encode(path, newInstance);
            });
    }

    @Override
    public <Target, Source> Optional<Decoder<Target, Source>> newDecoder(
        final EntityResolver resolver, final EncodingFactory<Target> factory
    ) {
        return mapping.<Target, Source>newDecoder(resolver, factory).map(
            parent -> (Decoder<Target, Source>) parent::decode);
    }

    public static ValueTypeDetector forAnnotation(
        final Class<? extends Annotation> annotation
    ) {
        return (resolver, sourceType) -> sourceType
            .findByAnnotation(JavaType::getMethods, annotation)
            .filter(m -> m.isPublic() && !m.isStatic())
            .flatMap(m -> {
                final Optional<CreatorMethod> creator = resolver.detectCreatorMethod(sourceType);

                if (!creator.isPresent()) {
                    return Stream.empty();
                }

                final CreatorMethod c = creator.get();

                if (c.fields().size() != 1) {
                    throw new IllegalArgumentException(
                        String.format("%s must have exactly one parameter, not %d", c,
                            c.fields().size()));
                }

                if (m.getParameters().size() != 0) {
                    throw new IllegalArgumentException(
                        String.format("@%s method must have no parameters: %s",
                            annotation.getSimpleName(), m));
                }

                /* type to serialize as */
                final TypeMapping targetType = resolver.mapping(m.getReturnType());

                return Stream.of(
                    new EntityValueTypeMapping(sourceType, targetType, c.instanceBuilder(), m));
            })
            .map(Match.withPriority(Priority.HIGH));
    }
}
