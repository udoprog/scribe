package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Stream;

@Data
public class Annotations {
    private final List<Annotation> annotations;

    public boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
        return annotations
            .stream()
            .filter(a -> annotation.isAssignableFrom(a.getClass()))
            .findFirst()
            .isPresent();
    }

    public <T extends Annotation> Stream<T> getAnnotation(final Class<T> annotation) {
        return annotations
            .stream()
            .filter(a -> annotation.isAssignableFrom(a.getClass()))
            .map(annotation::cast);
    }

    public static Annotations of(final Annotation... annotations) {
        return new Annotations(ImmutableList.copyOf(annotations));
    }

    public static Annotations empty() {
        return new Annotations(ImmutableList.of());
    }

    public Annotations merge(final Annotations a) {
        final ImmutableSet.Builder<Annotation> annotations = ImmutableSet.builder();

        annotations.addAll(this.annotations);
        annotations.addAll(a.annotations);

        return new Annotations(ImmutableList.copyOf(annotations.build()));
    }
}
