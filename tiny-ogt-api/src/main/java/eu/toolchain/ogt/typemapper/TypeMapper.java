package eu.toolchain.ogt.typemapper;

import eu.toolchain.ogt.EntityResolver;
import eu.toolchain.ogt.TypeMatcher;
import eu.toolchain.ogt.typemapping.TypeMapping;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Stream;

public interface TypeMapper {
    Stream<TypeMapping> map(final EntityResolver resolver, final Type type);

    static TypeMapper match(
        final TypeMatcher matcher, final Function<Type, TypeMapping> mapping
    ) {
        return (resolver, type) -> (matcher.matches(type) ? Stream.of(mapping.apply(type))
            : Stream.empty());
    }
}
