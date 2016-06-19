package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
public class Methods {
    private final List<Method> methods;

    public static Methods of(final Method... methods) {
        return new Methods(ImmutableList.copyOf(methods));
    }

    public static Methods empty() {
        return new Methods(ImmutableList.of());
    }

    public Stream<Method> getMethods(final String name, final Type... parameters) {
        return methods
            .stream()
            .filter(m -> m.getName().equals(name))
            .filter(m -> Arrays.equals(m.getGenericParameterTypes(), parameters));
    }
}
