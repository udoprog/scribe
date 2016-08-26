package eu.toolchain.scribe;

import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
public abstract class TypeReference<T> {
  private final Type type;

  public TypeReference() {
    final Type type = getClass().getGenericSuperclass();

    if (!(type instanceof ParameterizedType)) {
      throw new IllegalStateException(
          "TypeReference does not implement a parameterized type (" + type + ")");
    }

    final ParameterizedType pt = (ParameterizedType) type;

    this.type = pt.getActualTypeArguments()[0];
  }
}
