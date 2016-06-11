package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

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

    public <T extends Annotation> Optional<T> getAnnotation(final Class<T> annotation) {
        return annotations
            .stream()
            .filter(a -> annotation.isAssignableFrom(a.getClass()))
            .map(annotation::cast)
            .findFirst();
    }

    public static Annotations of(final Annotation... annotations) {
        return new Annotations(ImmutableList.copyOf(annotations));
    }

    public static Annotations empty() {
        return new Annotations(ImmutableList.of());
    }

    public Annotations merge(final Annotations a) {
        final ImmutableList.Builder<Annotation> annotations = ImmutableList.builder();

        annotations.addAll(this.annotations);
        annotations.addAll(a.annotations);

        return new Annotations(annotations.build());
    }
}
