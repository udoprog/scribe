package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;

import java.util.List;

public interface InstanceBuilder<Source> {
  /**
   * Build a new instance using the detected creator method.
   *
   * @param arguments Arguments to build new instance from.
   * @return The new instance.
   */
  Source newInstance(Context path, List<Object> arguments);

  JavaType getInstanceType();

  static StaticMethod<Object> fromStaticMethod(JavaType.Method method) {
    if (!method.isStatic() || !method.isPublic()) {
      throw new IllegalArgumentException("Method is not static and public (" + method + ")");
    }

    return new StaticMethod<>(method);
  }

  static Constructor<Object> fromConstructor(final JavaType.Constructor constructor) {
    if (!constructor.isPublic()) {
      throw new IllegalArgumentException("Constructor is not public (" + constructor + ")");
    }

    return new Constructor<>(constructor);
  }

  @Data
  class StaticMethod<Source> implements InstanceBuilder<Source> {
    private final JavaType.Method method;

    @SuppressWarnings("unchecked")
    @Override
    public Source newInstance(final Context path, final List<Object> arguments) {
      try {
        return (Source) method.invoke(null, arguments.toArray());
      } catch (final Exception e) {
        throw path.error("failed to create instance using static method (" + method + ")", e);
      }
    }

    @Override
    public JavaType getInstanceType() {
      return method.getReturnType();
    }
  }

  @Data
  class Constructor<Source> implements InstanceBuilder<Source> {
    private final JavaType.Constructor constructor;

    @SuppressWarnings("unchecked")
    @Override
    public Source newInstance(final Context path, final List<Object> arguments) {
      try {
        return (Source) constructor.newInstance(arguments.toArray());
      } catch (final Exception e) {
        throw path.error("failed to create instance using constructor (" + constructor + ")", e);
      }
    }

    @Override
    public JavaType getInstanceType() {
      return constructor.getType();
    }
  }
}
