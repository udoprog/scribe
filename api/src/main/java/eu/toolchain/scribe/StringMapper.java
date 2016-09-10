package eu.toolchain.scribe;

import java.lang.reflect.Type;

public interface StringMapper {
  StringEncoding<Object> stringEncodingForType(final Type type);

  @SuppressWarnings("unchecked")
  default <T> StringEncoding<T> stringEncodingFor(final TypeReference<T> type) {
    return (StringEncoding<T>) stringEncodingForType(type.getType());
  }

  @SuppressWarnings("unchecked")
  default <T> StringEncoding<T> stringEncodingFor(final Class<T> type) {
    return (StringEncoding<T>) stringEncodingForType(type);
  }
}
