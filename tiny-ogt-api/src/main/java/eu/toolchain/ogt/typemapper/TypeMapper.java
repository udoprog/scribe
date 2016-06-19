package eu.toolchain.ogt.typemapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.type.JavaType;
import eu.toolchain.ogt.TypeMatcher;
import eu.toolchain.ogt.typemapping.TypeMapping;

import java.util.function.Function;
import java.util.stream.Stream;

public interface TypeMapper {
    Stream<TypeMapping> map(final EntityResolver resolver, final JavaType type);

    static TypeMapper match(
        final TypeMatcher matcher, final Function<JavaType, TypeMapping> mapping
    ) {
        return (resolver, type) -> (matcher.matches(type) ? Stream.of(mapping.apply(type))
            : Stream.empty());
    }
}
