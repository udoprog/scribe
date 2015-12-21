package eu.toolchain.ogt;

import java.util.function.Function;

import eu.toolchain.ogt.type.TypeMapping;

public interface TypeEncodingProvider<T> {
    public TypeEncoding<Object, T> encodingFor(final JavaType type);

    public <C> TypeEncoding<C, T> encodingFor(final Class<C> type);

    public default <N> TypeEncodingProvider<N> convert(final Function<T, N> from,
            final Function<N, T> to) {
        return new TypeEncodingProvider<N>() {
            @Override
            public TypeEncoding<Object, N> encodingFor(JavaType type) {
                final TypeEncoding<Object, T> encoding =
                        TypeEncodingProvider.this.encodingFor(type);

                return new TypeEncoding<Object, N>() {
                    @Override
                    public N encode(Object instance) {
                        return from.apply((T) encoding.encode(instance));
                    }

                    @Override
                    public Object decode(N instance) {
                        return encoding.decode(to.apply(instance));
                    }

                    @Override
                    public TypeMapping mapping() {
                        return encoding.mapping();
                    }
                };
            }

            @Override
            public <C> TypeEncoding<C, N> encodingFor(Class<C> type) {
                final TypeEncoding<C, T> encoding = TypeEncodingProvider.this.encodingFor(type);

                return new TypeEncoding<C, N>() {
                    @Override
                    public N encode(C instance) {
                        return from.apply(encoding.encode(instance));
                    }

                    @Override
                    public C decode(N instance) {
                        return encoding.decode(to.apply(instance));
                    }

                    @Override
                    public TypeMapping mapping() {
                        return encoding.mapping();
                    }
                };
            }
        };
    }
}
