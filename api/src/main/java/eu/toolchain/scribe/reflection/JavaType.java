package eu.toolchain.scribe.reflection;

import eu.toolchain.scribe.ExecutableType;
import eu.toolchain.scribe.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = {"type", "typeParameters"})
public class JavaType implements AccessibleType, AnnotatedType {
  /**
   * All unboxed primitive classes to their boxed equivalent, excluding void.
   */
  public static final Map<JavaType, JavaType> PRIMITIVES_TO_BOXED = primitivesToBoxed();

  /**
   * All boxed primitive classes to their unboxed equivalent, excluding void.
   */
  public static final Map<JavaType, JavaType> PRIMITIVES_TO_UNBOXED = primitivesToUnboxed();

  /**
   * A collection of all possible unboxed primtives, excluding void.
   */
  public static final Set<JavaType> PRIMITIVES =
      Collections.unmodifiableSet(primitivesToBoxed().keySet());

  private final Optional<TypeVariableTree> parent;
  private final java.lang.Class<?> type;
  private final List<JavaType> typeParameters;

  /**
   * Check if the given type is boxed.
   * <p>
   * If {@code true}, then {@link #isPrimitive()} must also be {@code true}.
   *
   * @return {@code true} if this type is a boxed primitive type.
   */
  public boolean isBoxed() {
    return PRIMITIVES_TO_UNBOXED.containsKey(this);
  }

  /**
   * Return the boxed equivalent of this type.
   * <p>
   * Only effects primitive types.
   *
   * @return This type as a boxed type if possible. Otherwise this type.
   */
  public JavaType asBoxed() {
    return PRIMITIVES_TO_BOXED.getOrDefault(this, this);
  }

  /**
   * Return the unboxed equivalent of this type.
   * <p>
   * Only effects primitive types.
   *
   * @return This type as an unboxed type if possible. Otherwise this type.
   */
  public JavaType asUnboxed() {
    return PRIMITIVES_TO_UNBOXED.getOrDefault(this, this);
  }

  public boolean isPrimitive() {
    return PRIMITIVES_TO_BOXED.containsKey(this) || PRIMITIVES_TO_UNBOXED.containsKey(this);
  }

  public boolean isBoolean() {
    return type == boolean.class || type == Boolean.class;
  }

  public boolean isVoid() {
    return type == void.class || type == Void.class;
  }

  public boolean isArray() {
    return type.isArray();
  }

  public Optional<JavaType> getTypeParameter(final int index) {
    if (index < 0 || index >= typeParameters.size()) {
      return Optional.empty();
    }

    return Optional.of(typeParameters.get(index));
  }

  public Optional<Field> getField(final String field) {
    return getFields().filter(f -> f.getName().equals(field)).findFirst();
  }

  public Stream<Field> getFields() {
    return Arrays.stream(type.getDeclaredFields()).map(f -> {
      final JavaType fieldType = of(f.getGenericType(), parent);
      final List<Annotation> annotations = immutableCopy(f.getAnnotations());
      return new Field(annotations, f.getModifiers(), fieldType, f.getName());
    });
  }

  public Stream<Method> getMethod(final String name, final JavaType... parameterTypes) {
    final List<JavaType> parameters = immutableCopy(parameterTypes);

    return getMethods()
        .filter(m -> m.getName().equals(name))
        .filter(m -> m
            .getParameters()
            .stream()
            .map(Parameter::getParameterType)
            .collect(Collectors.toList())
            .equals(parameters));
  }

  public Stream<Method> getMethods() {
    return Arrays.stream(type.getDeclaredMethods()).map(m -> {
      final JavaType returnType = of(m.getGenericReturnType(), parent);
      final List<Parameter> parameters = buildParameters(m, parent);
      final List<Annotation> annotations = immutableCopy(m.getAnnotations());
      return new Method(m, annotations, m.getModifiers(), returnType, m.getName(), parameters);
    });
  }

  public Stream<Constructor> getConstructors() {
    return Arrays.stream(type.getDeclaredConstructors()).map(m -> {
      final List<Parameter> parameters = buildParameters(m, parent);
      return new Constructor(m, parameters, immutableCopy(m.getAnnotations()), m.getModifiers());
    });
  }

  public Optional<Constructor> getConstructor(final JavaType... parameterTypes) {
    final List<JavaType> parameters = immutableCopy(parameterTypes);

    return getConstructors()
        .filter(m -> m
            .getParameters()
            .stream()
            .map(Parameter::getParameterType)
            .collect(Collectors.toList())
            .equals(parameters))
        .findFirst();
  }

  @Override
  public int getModifiers() {
    return type.getModifiers();
  }

  @Override
  public Stream<Annotation> getAnnotationStream() {
    return Arrays.stream(type.getAnnotations());
  }

  public <E extends AnnotatedType, A extends Annotation> Stream<E> findByAnnotation(
      Function<JavaType, Stream<E>> function, Class<A> annotation
  ) {
    return function.apply(this).filter(m -> m.isAnnotationPresent(annotation));
  }

  @Override
  public String toString() {
    if (typeParameters.size() == 0) {
      return type.getCanonicalName();
    }

    final StringJoiner joiner = new StringJoiner(", ", "", "");
    typeParameters.stream().map(JavaType::toString).forEach(joiner::add);

    return type.getCanonicalName() + "<" + joiner.toString() + ">";
  }

  public static JavaType of(final TypeReference<?> reference) {
    return of(reference.getType(), Optional.empty());
  }

  public static JavaType of(final Type type) {
    return of(type, Optional.empty());
  }

  public static JavaType of(final Type type, final Optional<TypeVariableTree> parent) {
    if (type instanceof java.lang.Class<?>) {
      final Class<?> c = (Class<?>) type;

      final List<JavaType> typeParameters;

      if (c.getTypeParameters().length > 0) {
        final TypeVariableTree p =
            parent.orElseThrow(() -> new IllegalArgumentException("No type information available"));

        typeParameters =
            Arrays.stream(c.getTypeParameters()).map(p::lookup).collect(Collectors.toList());
      } else {
        typeParameters = Collections.emptyList();
      }

      return new JavaType(parent, c, typeParameters);
    }

    if (type instanceof java.lang.reflect.ParameterizedType) {
      final java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) type;
      final TypeVariableTree tree = TypeVariableTree.of(pt, parent);
      return of(pt.getRawType(), Optional.of(tree));
    }

    if (type instanceof TypeVariable<?>) {
      final TypeVariable<?> tv = (TypeVariable<?>) type;
      final TypeVariableTree p =
          parent.orElseThrow(() -> new IllegalStateException("Unable to lookup variable: " + tv));
      return p.lookup(tv);
    }

    throw new IllegalStateException("Unsupported type: " + type);
  }

  private static List<Parameter> buildParameters(
      final java.lang.reflect.Executable executable, final Optional<TypeVariableTree> parent
  ) {
    final ArrayList<Parameter> parameters = new ArrayList<>();

    int index = 0;

    for (final java.lang.reflect.Parameter p : executable.getParameters()) {
      final JavaType parameterType = of(executable.getGenericParameterTypes()[index++], parent);
      parameters.add(new Parameter(parameterType, immutableCopy(p.getAnnotations()), p.getName()));
    }

    return Collections.unmodifiableList(parameters);
  }

  private static Map<JavaType, JavaType> primitivesToBoxed() {
    final Map<Type, Type> forward = new HashMap<>();

    forward.put(boolean.class, Boolean.class);
    forward.put(byte.class, Byte.class);
    forward.put(char.class, Character.class);
    forward.put(short.class, Short.class);
    forward.put(int.class, Integer.class);
    forward.put(long.class, Long.class);
    forward.put(float.class, Float.class);
    forward.put(double.class, Double.class);

    return Collections.unmodifiableMap(forward
        .entrySet()
        .stream()
        .collect(Collectors.toMap(e -> JavaType.of(e.getKey()), e -> JavaType.of(e.getValue()))));
  }

  private static <T> List<T> immutableCopy(final T... arguments) {
    return Collections.unmodifiableList(Arrays.stream(arguments).collect(Collectors.toList()));
  }

  private static Map<JavaType, JavaType> primitivesToUnboxed() {
    return Collections.unmodifiableMap(primitivesToBoxed()
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
  }

  @Data
  public static class Parameter implements AnnotatedType {
    private final JavaType parameterType;
    private final List<Annotation> annotations;
    private final String name;

    @Override
    public Stream<Annotation> getAnnotationStream() {
      return annotations.stream();
    }
  }

  @Data
  @EqualsAndHashCode(exclude = {"type"})
  public static class Constructor implements ExecutableType, AccessibleType, AnnotatedType {
    private final java.lang.reflect.Constructor<?> type;

    private final List<Parameter> parameters;
    private final List<Annotation> annotations;
    private final int modifiers;

    public Object newInstance(Object... arguments)
        throws IllegalAccessException, InvocationTargetException, InstantiationException {
      return type.newInstance(arguments);
    }

    @Override
    public Stream<Annotation> getAnnotationStream() {
      return Arrays.stream(type.getAnnotations());
    }
  }

  @Data
  @EqualsAndHashCode(exclude = {"type"})
  public static class Method implements ExecutableType, AccessibleType, AnnotatedType {
    private final java.lang.reflect.Method type;

    private final List<Annotation> annotations;
    private final int modifiers;
    private final JavaType returnType;
    private final String name;
    private final List<Parameter> parameters;

    @Override
    public Stream<Annotation> getAnnotationStream() {
      return annotations.stream();
    }

    public Object invoke(final Object instance, final Object... arguments)
        throws InvocationTargetException, IllegalAccessException {
      return type.invoke(instance, arguments);
    }
  }

  @Data
  public static class Field implements AccessibleType, AnnotatedType {
    private final List<Annotation> annotations;
    private final int modifiers;
    private final JavaType fieldType;
    private final String name;

    @Override
    public Stream<Annotation> getAnnotationStream() {
      return annotations.stream();
    }
  }
}
